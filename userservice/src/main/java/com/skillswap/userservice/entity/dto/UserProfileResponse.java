package com.skillswap.userservice.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserProfileResponse {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String bio;
    private List<SkillDTO> skillsOfferedDetails;
    private List<SkillDTO> skillsWantedDetails;
}