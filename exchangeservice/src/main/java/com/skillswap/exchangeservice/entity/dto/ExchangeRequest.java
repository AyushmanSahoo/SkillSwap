package com.skillswap.exchangeservice.entity.dto;

import lombok.Data;

@Data
public class ExchangeRequest {
    private Long providerId;
    private Long requesterSkillId;
    private Long providerSkillId;
}