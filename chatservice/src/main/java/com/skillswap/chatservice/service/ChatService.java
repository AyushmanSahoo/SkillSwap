package com.skillswap.chatservice.service;


import com.skillswap.chatservice.entity.Message;
import com.skillswap.chatservice.entity.dto.ChatMessage;
import com.skillswap.chatservice.entity.dto.ExchangeDTO;
import com.skillswap.chatservice.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final MessageRepository messageRepository;
    private final WebClient exchangeWebClient;

    /**
     * This is the security check required by the architecture.
     * It calls the ExchangeService to verify the user is a participant
     * in the exchange they are trying to chat in.
     */
    public boolean isUserParticipantInExchange(Long userId, Long exchangeId) {
        try {
            // Call ExchangeService to get participants
            ExchangeDTO exchange = exchangeWebClient.get()
                .uri("/api/v1/exchanges/{id}", exchangeId)
                .retrieve()
                .bodyToMono(ExchangeDTO.class)
                .block(); // Block to make it synchronous

            if (exchange != null && exchange.getRequester() != null && exchange.getProvider() != null) {
                Long requesterId = exchange.getRequester().getUserId();
                Long providerId = exchange.getProvider().getUserId();
                
                // Check if the user is one of the two participants
                return userId.equals(requesterId) || userId.equals(providerId);
            }
        } catch (Exception e) {
            log.error("Failed to verify user participation for exchange {}: {}", exchangeId, e.getMessage());
        }
        return false;
    }

    @Transactional
    public Message saveMessage(ChatMessage chatMessage, Long senderId, Long exchangeId) {
        Message message = new Message();
        message.setExchangeId(exchangeId);
        message.setSenderId(senderId);
        message.setRecipientId(chatMessage.getRecipientId());
        message.setContent(chatMessage.getContent());
        message.setTimestamp(LocalDateTime.now());
        
        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<Message> getChatHistory(Long exchangeId) {
        return messageRepository.findByExchangeIdOrderByTimestampAsc(exchangeId);
    }
}