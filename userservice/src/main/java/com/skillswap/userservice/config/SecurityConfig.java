package com.skillswap.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .authorizeHttpRequests(authorize -> authorize
//                .requestMatchers("/actuator/health").permitAll()
//                // Allow user creation (called by AuthService)
//                .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
//                // Allow other services to get user data
//                .requestMatchers(HttpMethod.GET, "/api/v1/users/**").permitAll()
//                .anyRequest().authenticated()
//            )
//            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
//            .csrf(AbstractHttpConfigurer::disable)
//            .sessionManagement(session ->
//                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Allow all requests to any endpoint
                        .requestMatchers("/**").permitAll()
                        .anyRequest().permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable);
        // No .oauth2ResourceServer() needed

        return http.build();
    }
}