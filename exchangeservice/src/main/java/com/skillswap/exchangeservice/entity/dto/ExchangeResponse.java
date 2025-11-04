package com.skillswap.exchangeservice.entity.dto;

import com.skillswap.exchangeservice.entity.ExchangeStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExchangeResponse {
    private Long id;
    private ExchangeStatus status;
    private LocalDateTime requestTimestamp;
    private LocalDateTime responseTimestamp;
    private LocalDateTime completedTimestamp;
    
    // Enriched data from other services
    private UserDTO requester;
    private UserDTO provider;
    private SkillDTO requesterSkill;
    private SkillDTO providerSkill;
}