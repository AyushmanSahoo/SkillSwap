package com.skillswap.userservice.service;


import com.skillswap.userservice.entity.SkillType;
import com.skillswap.userservice.entity.UserProfile;
import com.skillswap.userservice.entity.UserSkill;
import com.skillswap.userservice.entity.dto.SkillDTO;
import com.skillswap.userservice.entity.dto.UserCreateRequest;
import com.skillswap.userservice.entity.dto.UserProfileResponse;
import com.skillswap.userservice.entity.dto.UserUpdateRequest;
import com.skillswap.userservice.repository.UserProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {

    private final UserProfileRepository userRepository;
    private final WebClient skillWebClient; // For calling Skill Service 

    // Helper method to find user or throw exception
    private UserProfile findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
    }

    @Transactional
    public UserProfile createUserProfile(UserCreateRequest req) {
        UserProfile profile = new UserProfile();
        profile.setUserId(req.getUserId());
        profile.setFirstName(req.getFirstName());
        profile.setLastName(req.getLastName());
        profile.setEmail(req.getEmail());
        // Save the new user
        return userRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfileWithSkills(Long userId) {
        UserProfile profile = findUserById(userId);
        
        Set<Long> skillIds = profile.getSkills().stream()
            .map(UserSkill::getSkillId)
            .collect(Collectors.toSet());
        
        // Call Skill Service to get skill details 
        Map<Long, SkillDTO> skillMap = fetchSkillsAsMap(skillIds);
        
        return buildResponseDTO(profile, skillMap);
    }

    @Transactional
    public UserProfile updateUserProfile(Long userId, UserUpdateRequest req) {
        UserProfile profile = findUserById(userId);
        
        profile.setFirstName(req.getFirstName());
        profile.setLastName(req.getLastName());
        profile.setBio(req.getBio());

        // Thanks to 'orphanRemoval = true', this is all we need to do
        // to update the user_skills table.
        profile.getSkills().clear(); // Clear old skills
        if (req.getSkills() != null) {
            Set<UserSkill> newUserSkills = req.getSkills().stream()
                .map(skillReq -> {
                    UserSkill us = new UserSkill();
                    us.setSkillId(skillReq.getSkillId());
                    us.setType(skillReq.getType());
                    us.setUser(profile); // Link back to the user
                    return us;
                })
                .collect(Collectors.toSet());
            profile.getSkills().addAll(newUserSkills); // Add new skills
        }
        
        // The 'cascade' will save all changes to user_skills
        return userRepository.save(profile);
    }

    @Transactional
    public void deleteUserProfile(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found: " + userId);
        }
        // 'cascade' will also delete all of this user's 'user_skills'
        userRepository.deleteById(userId);
    }

    /**
     * Calls the Skill Service to fetch details for a set of IDs.
     * Uses .block() to make the WebClient call synchronous,
     * which is standard in a blocking JPA service.
     */
    private Map<Long, SkillDTO> fetchSkillsAsMap(Set<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            return Map.of();
        }
        try {
            List<SkillDTO> skills = skillWebClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/api/v1/skills/batch")
                    .queryParam("ids", skillIds)
                    .build())
                .retrieve()
                .bodyToFlux(SkillDTO.class)
                .collectList()
                .block(); // Make the call blocking

            if (skills == null) return Map.of();
            
            return skills.stream().collect(Collectors.toMap(SkillDTO::getId, s -> s));
        } catch (Exception e) {
            log.error("Failed to fetch skills from SkillService", e);
            return Map.of();
        }
    }
    
    // Helper to build the final JSON response
    private UserProfileResponse buildResponseDTO(UserProfile p, Map<Long, SkillDTO> skillMap) {
        UserProfileResponse res = new UserProfileResponse();
        res.setUserId(p.getUserId());
        res.setFirstName(p.getFirstName());
        res.setLastName(p.getLastName());
        res.setEmail(p.getEmail());
        res.setBio(p.getBio());

        res.setSkillsOfferedDetails(
            p.getSkills().stream()
                .filter(s -> s.getType() == SkillType.OFFERED)
                .map(s -> skillMap.get(s.getSkillId()))
                .filter(java.util.Objects::nonNull)
                .toList()
        );
        res.setSkillsWantedDetails(
            p.getSkills().stream()
                .filter(s -> s.getType() == SkillType.WANTED)
                .map(s -> skillMap.get(s.getSkillId()))
                .filter(java.util.Objects::nonNull)
                .toList()
        );
        return res;
    }
}