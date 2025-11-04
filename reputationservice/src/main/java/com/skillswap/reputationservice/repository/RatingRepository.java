package com.skillswap.reputationservice.repository;

import com.skillswap.reputationservice.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    // Find all ratings a user has received
    List<Rating> findByRateeId(Long rateeId);

    // Check if a rating already exists for this exchange from this rater
    Optional<Rating> findByExchangeIdAndRaterId(Long exchangeId, Long raterId);
    
    // Calculate the average score for a user
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.rateeId = :rateeId")
    Double findAverageScoreByRateeId(Long rateeId);
}