package com.skillswap.reputationservice.service;


import com.skillswap.reputationservice.entity.Rating;
import com.skillswap.reputationservice.entity.dto.RatingRequest;
import com.skillswap.reputationservice.entity.dto.UserReputationResponse;
import com.skillswap.reputationservice.repository.RatingRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReputationService {

    private final RatingRepository ratingRepository;

    @Transactional
    public Rating createRating(RatingRequest req) {
        // Prevent duplicate ratings for the same exchange
        ratingRepository.findByExchangeIdAndRaterId(req.getExchangeId(), req.getRaterId())
            .ifPresent(rating -> {
                throw new EntityExistsException("Rating for this exchange already submitted.");
            });

        Rating rating = new Rating();
        rating.setExchangeId(req.getExchangeId());
        rating.setRaterId(req.getRaterId());
        rating.setRateeId(req.getRateeId());
        rating.setScore(req.getScore());
        rating.setComment(req.getComment());
        rating.setTimestamp(LocalDateTime.now());
        
        return ratingRepository.save(rating);
    }

    @Transactional(readOnly = true)
    public UserReputationResponse getUserReputation(Long userId) {
        List<Rating> receivedRatings = ratingRepository.findByRateeId(userId);
        Double averageScore = ratingRepository.findAverageScoreByRateeId(userId);

        UserReputationResponse response = new UserReputationResponse();
        response.setUserId(userId);
        response.setRatings(receivedRatings);
        response.setTotalRatings(receivedRatings.size());
        response.setAverageScore(averageScore != null ? averageScore : 0.0);
        
        return response;
    }
}