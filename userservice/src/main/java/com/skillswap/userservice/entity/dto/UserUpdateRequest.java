package com.skillswap.userservice.entity.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserUpdateRequest {
    private String firstName;
    private String lastName;
    private String bio;
    private Set<UserSkillRequest> skills;
}