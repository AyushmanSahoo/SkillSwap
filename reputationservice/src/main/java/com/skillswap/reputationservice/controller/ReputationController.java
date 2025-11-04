package com.skillswap.reputationservice.controller;


import com.skillswap.reputationservice.entity.Rating;
import com.skillswap.reputationservice.entity.dto.RatingRequest;
import com.skillswap.reputationservice.entity.dto.UserReputationResponse;
import com.skillswap.reputationservice.service.ReputationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReputationController {

    private final ReputationService reputationService;

    /**
     * Endpoint for the ExchangeService to submit a new rating.
     */
    @PostMapping("/ratings")
    public ResponseEntity<Rating> createRating(@RequestBody RatingRequest request) {
        Rating newRating = reputationService.createRating(request);
        return new ResponseEntity<>(newRating, HttpStatus.CREATED);
    }

    /**
     * Public endpoint to get the reputation for a specific user.
     */
    @GetMapping("/reputation/user/{userId}")
    public ResponseEntity<UserReputationResponse> getUserReputation(@PathVariable Long userId) {
        UserReputationResponse reputation = reputationService.getUserReputation(userId);
        return ResponseEntity.ok(reputation);
    }
}