package com.skillswap.exchangeservice.entity.dto;

import lombok.Data;

@Data
public class NotificationRequest {
    private String toEmail;
    private String subject;
    private String body;
}