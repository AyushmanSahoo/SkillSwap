package com.skillswap.exchangeservice.service;

import com.skillswap.exchangeservice.entity.Exchange;
import com.skillswap.exchangeservice.entity.ExchangeStatus;
import com.skillswap.exchangeservice.entity.dto.*;
import com.skillswap.exchangeservice.repository.ExchangeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeService {

    private final ExchangeRepository exchangeRepository;
    
    // Injected WebClients from WebClientConfig
    private final WebClient userWebClient;
    private final WebClient skillWebClient;
    private final WebClient notificationWebClient;
    private final WebClient reputationWebClient;

    private Exchange findExchangeById(Long id) {
        return exchangeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Exchange not found: " + id));
    }

    // --- Core Methods ---

    @Transactional
    public ExchangeResponse createExchange(ExchangeRequest req, Long requesterId) {
        if (requesterId.equals(req.getProviderId())) {
            throw new IllegalArgumentException("Cannot create an exchange with yourself.");
        }

        // 1. Validate Users and Skills by calling other services [cite: 26]
        UserDTO requester = fetchUser(requesterId)
            .blockOptional()
            .orElseThrow(() -> new EntityNotFoundException("Requester not found: " + requesterId));
        
        UserDTO provider = fetchUser(req.getProviderId())
            .blockOptional()
            .orElseThrow(() -> new EntityNotFoundException("Provider not found: " + req.getProviderId()));
        
        SkillDTO requesterSkill = fetchSkill(req.getRequesterSkillId())
            .blockOptional()
            .orElseThrow(() -> new EntityNotFoundException("Requester skill not found: " + req.getRequesterSkillId()));

        SkillDTO providerSkill = fetchSkill(req.getProviderSkillId())
            .blockOptional()
            .orElseThrow(() -> new EntityNotFoundException("Provider skill not found: " + req.getProviderSkillId()));

        // 2. Create and save the exchange
        Exchange exchange = new Exchange();
        exchange.setRequesterId(requesterId);
        exchange.setProviderId(req.getProviderId());
        exchange.setRequesterSkillId(req.getRequesterSkillId());
        exchange.setProviderSkillId(req.getProviderSkillId());
        exchange.setStatus(ExchangeStatus.PENDING);
        exchange.setRequestTimestamp(LocalDateTime.now());
        
        Exchange savedExchange = exchangeRepository.save(exchange);

        // 3. Call Notification Service to inform the provider 
        sendNotification(
            provider.getEmail(),
            "New SkillSwap Request!",
            requester.getFirstName() + " wants to swap " + 
            requesterSkill.getName() + " for your " + providerSkill.getName() + "."
        );

        return buildResponse(savedExchange, requester, provider, requesterSkill, providerSkill);
    }

    @Transactional
    public ExchangeResponse respondToExchange(Long exchangeId, ExchangeUpdateDTO update, Long providerId) {
        Exchange exchange = findExchangeById(exchangeId);

        // Security check: Only the provider can respond
        if (!exchange.getProviderId().equals(providerId)) {
            throw new SecurityException("Only the provider can respond to this request.");
        }
        if (exchange.getStatus() != ExchangeStatus.PENDING) {
            throw new IllegalStateException("This exchange is no longer pending.");
        }

        exchange.setStatus(update.getStatus()); // Must be ACCEPTED or REJECTED
        exchange.setResponseTimestamp(LocalDateTime.now());
        
        Exchange savedExchange = exchangeRepository.save(exchange);
        
        // Fetch user/skill info for notification
        UserDTO requester = fetchUser(exchange.getRequesterId()).block();
        if (requester != null) {
            // Call Notification Service to inform the requester 
            sendNotification(
                requester.getEmail(),
                "SkillSwap Request Updated!",
                "Your request to swap skills has been " + update.getStatus().name()
            );
        }
        
        return getExchangeById(exchangeId); // Return the fully enriched response
    }
    
    @Transactional
    public ExchangeResponse completeExchange(Long exchangeId, Long userId) {
        Exchange exchange = findExchangeById(exchangeId);

        // Security check: Only a participant can complete it
        if (!exchange.getProviderId().equals(userId) && !exchange.getRequesterId().equals(userId)) {
            throw new SecurityException("Only a participant can complete this exchange.");
        }
        if (exchange.getStatus() != ExchangeStatus.ACCEPTED) {
            throw new IllegalStateException("Only an accepted exchange can be completed.");
        }

        exchange.setStatus(ExchangeStatus.COMPLETED);
        exchange.setCompletedTimestamp(LocalDateTime.now());
        exchangeRepository.save(exchange);
        
        return getExchangeById(exchangeId);
    }

    @Transactional
    public void submitFeedback(Long exchangeId, RatingRequest rating, Long raterId) {
        Exchange exchange = findExchangeById(exchangeId);

        // Security check
        if (!exchange.getProviderId().equals(raterId) && !exchange.getRequesterId().equals(raterId)) {
            throw new SecurityException("Only a participant can submit feedback.");
        }
        if (exchange.getStatus() != ExchangeStatus.COMPLETED) {
            throw new IllegalStateException("Feedback can only be left for completed exchanges.");
        }
        
        // Determine who is being rated
        Long rateeId = exchange.getRequesterId().equals(raterId) ? exchange.getProviderId() : exchange.getRequesterId();
        
        rating.setExchangeId(exchangeId);
        rating.setRaterId(raterId);
        rating.setRateeId(rateeId);

        // 1. Call Reputation Service to submit feedback 
        log.info("Submitting feedback for exchange: {}", exchangeId);
        reputationWebClient.post()
            .uri("/api/v1/ratings")
            .bodyValue(rating)
            .retrieve()
            .bodyToMono(Void.class)
            .doOnError(e -> log.error("Failed to submit rating", e))
            .subscribe();
    }


    // --- Read Methods ---

    @Transactional(readOnly = true)
    public ExchangeResponse getExchangeById(Long exchangeId) {
        Exchange exchange = findExchangeById(exchangeId);
        
        // Fork-Join: Fetch all required external data in parallel
        Mono<UserDTO> requesterMono = fetchUser(exchange.getRequesterId());
        Mono<UserDTO> providerMono = fetchUser(exchange.getProviderId());
        Mono<SkillDTO> reqSkillMono = fetchSkill(exchange.getRequesterSkillId());
        Mono<SkillDTO> provSkillMono = fetchSkill(exchange.getProviderSkillId());

        return Mono.zip(requesterMono, providerMono, reqSkillMono, provSkillMono)
            .map(tuple -> buildResponse(
                exchange, 
                tuple.getT1(), 
                tuple.getT2(), 
                tuple.getT3(), 
                tuple.getT4()
            ))
            .block(); // Block to make it synchronous for the controller
    }

    @Transactional(readOnly = true)
    public List<ExchangeResponse> getExchangesForUser(Long userId) {
        return exchangeRepository.findByRequesterIdOrProviderId(userId, userId)
            .stream()
                // CORRECT: Gets the ID (a Long) from the Exchange and passes it
                .map(exchange -> this.getExchangeById(exchange.getId()))
            .collect(Collectors.toList());
    }


    // --- WebClient Helper Methods ---

    // Calls User Service [cite: 26]
    private Mono<UserDTO> fetchUser(Long userId) {
        return userWebClient.get()
            .uri("/api/v1/users/{id}", userId)
            .retrieve()
            .bodyToMono(UserDTO.class)
            .doOnError(e -> log.error("Failed to fetch user {}", userId, e))
            .onErrorResume(e -> Mono.empty()); // Return empty if not found
    }

    // Calls Skill Service [cite: 26]
    private Mono<SkillDTO> fetchSkill(Long skillId) {
        return skillWebClient.get()
            .uri("/api/v1/skills/{id}", skillId)
            .retrieve()
            .bodyToMono(SkillDTO.class)
            .doOnError(e -> log.error("Failed to fetch skill {}", skillId, e))
            .onErrorResume(e -> Mono.empty());
    }

    // Calls Notification Service 
    private void sendNotification(String email, String subject, String body) {
        NotificationRequest req = new NotificationRequest();
        req.setToEmail(email);
        req.setSubject(subject);
        req.setBody(body);
        
        notificationWebClient.post()
            .uri("/api/v1/notify/email") // (Assuming endpoint)
            .bodyValue(req)
            .retrieve()
            .bodyToMono(Void.class)
            .doOnSuccess(v -> log.info("Notification sent to {}", email))
            .doOnError(e -> log.error("Failed to send notification", e))
            .subscribe(); // Fire-and-forget
    }

    // --- DTO Builder ---
    private ExchangeResponse buildResponse(Exchange ex, UserDTO req, UserDTO prov, SkillDTO reqSkill, SkillDTO provSkill) {
        ExchangeResponse res = new ExchangeResponse();
        res.setId(ex.getId());
        res.setStatus(ex.getStatus());
        res.setRequestTimestamp(ex.getRequestTimestamp());
        res.setResponseTimestamp(ex.getResponseTimestamp());
        res.setCompletedTimestamp(ex.getCompletedTimestamp());
        res.setRequester(req);
        res.setProvider(prov);
        res.setRequesterSkill(reqSkill);
        res.setProviderSkill(provSkill);
        return res;
    }
}