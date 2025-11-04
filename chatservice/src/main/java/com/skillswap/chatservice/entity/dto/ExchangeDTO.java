package com.skillswap.chatservice.entity.dto;

import lombok.Data;

@Data
public class ExchangeDTO {
    // We only need to know who the participants are
    private UserDTO requester;
    private UserDTO provider;

    @Data
    public static class UserDTO {
        private Long userId;
    }
}