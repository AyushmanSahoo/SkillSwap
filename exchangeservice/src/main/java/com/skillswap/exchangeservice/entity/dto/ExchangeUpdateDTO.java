package com.skillswap.exchangeservice.entity.dto;

import com.skillswap.exchangeservice.entity.ExchangeStatus;
import lombok.Data;

@Data
public class ExchangeUpdateDTO {
    private ExchangeStatus status; // e.g., ACCEPTED, REJECTED, CANCELLED
}