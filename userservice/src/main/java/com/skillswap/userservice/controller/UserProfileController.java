package com.skillswap.userservice.controller;


import com.skillswap.userservice.entity.UserProfile;
import com.skillswap.userservice.entity.dto.UserCreateRequest;
import com.skillswap.userservice.entity.dto.UserProfileResponse;
import com.skillswap.userservice.entity.dto.UserUpdateRequest;
import com.skillswap.userservice.service.UserProfileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userService;

    // POST /api/v1/users
    @PostMapping
    public ResponseEntity<UserProfile> createUser(@RequestBody UserCreateRequest req) {
        UserProfile profile = userService.createUserProfile(req);
        return new ResponseEntity<>(profile, HttpStatus.CREATED);
    }

    // GET /api/v1/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getUserProfileWithSkills(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT /api/v1/users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<UserProfile> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest req) {
        try {
            // TODO: Add security check: ensure authenticated user ID == id
            UserProfile profile = userService.updateUserProfile(id, req);
            return ResponseEntity.ok(profile);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/v1/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            // TODO: Add security check: ensure authenticated user ID == id
            userService.deleteUserProfile(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}