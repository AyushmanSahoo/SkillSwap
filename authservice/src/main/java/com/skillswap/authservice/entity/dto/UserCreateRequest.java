package com.skillswap.authservice.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// This DTO must match the UserCreateRequest in the UserService
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequest {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
}