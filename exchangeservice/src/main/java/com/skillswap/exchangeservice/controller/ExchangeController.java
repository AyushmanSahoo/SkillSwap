package com.skillswap.exchangeservice.controller;


import com.skillswap.exchangeservice.entity.dto.ExchangeRequest;
import com.skillswap.exchangeservice.entity.dto.ExchangeResponse;
import com.skillswap.exchangeservice.entity.dto.ExchangeUpdateDTO;
import com.skillswap.exchangeservice.entity.dto.RatingRequest;
import com.skillswap.exchangeservice.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exchanges")
@RequiredArgsConstructor
public class ExchangeController {

    private final ExchangeService exchangeService;

    // Helper to get User ID from JWT
    private Long getUserIdFromJwt(Jwt jwt) {
        return Long.parseLong(jwt.getSubject());
    }

    @PostMapping
    public ResponseEntity<ExchangeResponse> createExchange(
            @RequestBody ExchangeRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long requesterId = getUserIdFromJwt(jwt);
        ExchangeResponse response = exchangeService.createExchange(request, requesterId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExchangeResponse> getExchangeById(@PathVariable Long id) {
        return ResponseEntity.ok(exchangeService.getExchangeById(id));
    }

    @GetMapping("/my-exchanges")
    public ResponseEntity<List<ExchangeResponse>> getMyExchanges(@AuthenticationPrincipal Jwt jwt) {
        Long userId = getUserIdFromJwt(jwt);
        return ResponseEntity.ok(exchangeService.getExchangesForUser(userId));
    }

    @PutMapping("/{id}/respond")
    public ResponseEntity<ExchangeResponse> respondToExchange(
            @PathVariable Long id,
            @RequestBody ExchangeUpdateDTO update,
            @AuthenticationPrincipal Jwt jwt) {
        Long providerId = getUserIdFromJwt(jwt);
        ExchangeResponse response = exchangeService.respondToExchange(id, update, providerId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/complete")
    public ResponseEntity<ExchangeResponse> completeExchange(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = getUserIdFromJwt(jwt);
        ExchangeResponse response = exchangeService.completeExchange(id, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/feedback")
    public ResponseEntity<Void> submitFeedback(
            @PathVariable Long id,
            @RequestBody RatingRequest rating,
            @AuthenticationPrincipal Jwt jwt) {
        Long raterId = getUserIdFromJwt(jwt);
        exchangeService.submitFeedback(id, rating, raterId);
        return ResponseEntity.ok().build();
    }
}