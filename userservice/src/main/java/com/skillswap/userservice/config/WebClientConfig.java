package com.skillswap.userservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${services.skill-service.base-url}")
    private String skillServiceBaseUrl;

    @Bean
    public WebClient skillWebClient(WebClient.Builder builder) {
        return builder.baseUrl(skillServiceBaseUrl).build();
    }
}