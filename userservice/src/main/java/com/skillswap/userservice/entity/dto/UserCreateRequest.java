package com.skillswap.userservice.entity.dto;

import com.skillswap.userservice.entity.SkillType;
import lombok.Data;
import java.util.List;
import java.util.Set;

// Used when creating a new user
@Data
public class UserCreateRequest {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
}


