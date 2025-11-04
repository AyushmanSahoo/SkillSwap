package com.skillswap.userservice.entity.dto;

import com.skillswap.userservice.entity.SkillType;
import lombok.Data;

// Part of the UserUpdateRequest
@Data
public class UserSkillRequest {
    private Long skillId;
    private SkillType type;
}