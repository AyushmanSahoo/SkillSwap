package com.skillswap.chatservice.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    // senderId and exchangeId will be set from the context
    private Long recipientId;
    private String content;
}