package com.skillswap.chatservice.controller;

import com.skillswap.chatservice.entity.Message;
import com.skillswap.chatservice.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatHistoryController {

    private final ChatService chatService;

    /**
     * A REST endpoint to get all historical messages for an exchange.
     * This is typically called when the user first opens the chat window.
     */
    @GetMapping("/history/{exchangeId}")
    public ResponseEntity<List<Message>> getChatHistory(
            @PathVariable Long exchangeId,
            @AuthenticationPrincipal Jwt jwt) { // Get user from HTTP JWT
        
        Long userId = Long.parseLong(jwt.getSubject());

        // Security Check: Verify the user is allowed to see this history
        if (!chatService.isUserParticipantInExchange(userId, exchangeId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Message> history = chatService.getChatHistory(exchangeId);
        return ResponseEntity.ok(history);
    }
}