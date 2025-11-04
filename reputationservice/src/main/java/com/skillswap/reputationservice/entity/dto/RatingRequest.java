package com.skillswap.reputationservice.entity.dto;

import lombok.Data;

@Data
public class RatingRequest {
    // This DTO is sent by the ExchangeService
    private Long exchangeId;
    private Long raterId;
    private Long rateeId;
    private int score; 
    private String comment;
}