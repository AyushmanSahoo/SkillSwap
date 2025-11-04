package com.skillswap.authservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${services.user-service.base-url}")
    private String userServiceBaseUrl;

    @Bean
    public WebClient userWebClient(WebClient.Builder builder) {
        return builder.baseUrl(userServiceBaseUrl).build();
    }
}