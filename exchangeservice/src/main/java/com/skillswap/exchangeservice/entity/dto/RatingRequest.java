package com.skillswap.exchangeservice.entity.dto;

import lombok.Data;
//A DTO to send to the reputation-service
@Data
public class RatingRequest {
    private Long exchangeId;
    private Long raterId;
    private Long rateeId;
    private int score; // e.g., 1-5
    private String comment;
}