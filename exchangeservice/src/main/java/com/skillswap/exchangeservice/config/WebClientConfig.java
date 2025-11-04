package com.skillswap.exchangeservice.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${services.user-service.base-url}")
    private String userServiceBaseUrl;

    @Value("${services.skill-service.base-url}")
    private String skillServiceBaseUrl;

    @Value("${services.notification-service.base-url}")
    private String notificationServiceBaseUrl;

    @Value("${services.reputation-service.base-url}")
    private String reputationServiceBaseUrl;

    @Bean
    @Qualifier("userWebClient")
    public WebClient userWebClient(WebClient.Builder builder) {
        return builder.baseUrl(userServiceBaseUrl).build();
    }

    @Bean
    @Qualifier("skillWebClient")
    public WebClient skillWebClient(WebClient.Builder builder) {
        return builder.baseUrl(skillServiceBaseUrl).build();
    }

    @Bean
    @Qualifier("notificationWebClient")
    public WebClient notificationWebClient(WebClient.Builder builder) {
        return builder.baseUrl(notificationServiceBaseUrl).build();
    }

    @Bean
    @Qualifier("reputationWebClient")
    public WebClient reputationWebClient(WebClient.Builder builder) {
        return builder.baseUrl(reputationServiceBaseUrl).build();
    }
}