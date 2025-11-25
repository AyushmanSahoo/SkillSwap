package com.skillswap.notificationservice.config;

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
//                // Allow health checks
//                .requestMatchers("/actuator/health").permitAll()
//
//                // Secure the email endpoint
//                .requestMatchers(HttpMethod.POST, "/api/v1/notify/email").authenticated()
//
//                .anyRequest().denyAll() // Deny any other unconfigured requests
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