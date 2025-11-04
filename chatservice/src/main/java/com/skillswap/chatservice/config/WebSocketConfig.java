package com.skillswap.chatservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Enable WebSocket message handling
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // The endpoint for clients to connect to
        // /ws is the HTTP URL for the WebSocket handshake
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*"); // Allow all origins
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // "/app" is the prefix for messages from clients to the server (e.g., /app/chat/1)
        registry.setApplicationDestinationPrefixes("/app");
        
        // "/queue" is for 1-to-1 messages.
        registry.enableSimpleBroker("/queue");
        
        // Use "/user" as the prefix for user-specific destinations
        // This allows SimpMessagingTemplate.convertAndSendToUser() to work
        registry.setUserDestinationPrefix("/user");
    }
}