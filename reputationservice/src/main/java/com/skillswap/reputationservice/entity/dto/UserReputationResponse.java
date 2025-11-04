package com.skillswap.reputationservice.entity.dto;

import com.skillswap.reputationservice.entity.Rating;
import lombok.Data;
import java.util.List;

@Data
public class UserReputationResponse {
    private Long userId;
    private Double averageScore;
    private int totalRatings;
    private List<Rating> ratings;
}