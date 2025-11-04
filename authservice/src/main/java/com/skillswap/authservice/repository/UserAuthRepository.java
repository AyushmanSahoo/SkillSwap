package com.skillswap.authservice.repository;

import com.skillswap.authservice.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {

    // This method is essential for Spring Security to find a user by their
    // email (which we use as the username) during the login process.
    Optional<UserAuth> findByEmail(String email);
}