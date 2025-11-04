package com.skillswap.chatservice.controller;


import com.skillswap.chatservice.entity.Message;
import com.skillswap.chatservice.entity.dto.ChatMessage;
import com.skillswap.chatservice.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    // Used to send messages to specific users
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    /**
     * Handles messages sent to /app/chat/{exchangeId}
     * This method is the core of the 1-to-1 chat.
     */
    @MessageMapping("/chat/{exchangeId}")
    public void sendMessage(@Payload ChatMessage chatMessage,
                            @DestinationVariable Long exchangeId,
                            Principal principal) { // principal is injected by our interceptor
        
        // The "name" of the JWT principal is the user's ID (the 'sub' claim)
        Long senderId = Long.parseLong(principal.getName()); 
        
        // 1. SECURITY: Verify user is a participant in this exchange
        if (!chatService.isUserParticipantInExchange(senderId, exchangeId)) {
            log.warn("User {} is not authorized to chat in exchange {}", senderId, exchangeId);
            // (Optional) Send an error message back to the sender
            messagingTemplate.convertAndSendToUser(
                principal.getName(), 
                "/queue/errors", 
                "You are not authorized to participate in this chat."
            );
            return;
        }

        // 2. Save the message to the database
        Message savedMessage = chatService.saveMessage(chatMessage, senderId, exchangeId);
        
        // 3. Send the message to the recipient's private queue
        String recipientIdStr = String.valueOf(chatMessage.getRecipientId());
        messagingTemplate.convertAndSendToUser(
            recipientIdStr,         // The user ID to send to
            "/queue/messages",      // The destination
            savedMessage            // The message payload
        );

        // 4. (Optional) Send the message back to the sender as confirmation
        messagingTemplate.convertAndSendToUser(
            principal.getName(),    // The sender's user ID
            "/queue/messages",      // The destination
            savedMessage            // The message payload
        );
        
        log.info("Message sent from {} to {} in exchange {}", 
            senderId, chatMessage.getRecipientId(), exchangeId);
    }
}