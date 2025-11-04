package com.skillswap.chatservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${services.exchange-service.base-url}")
    private String exchangeServiceBaseUrl;

    @Bean
    public WebClient exchangeWebClient(WebClient.Builder builder) {
        return builder.baseUrl(exchangeServiceBaseUrl).build();
    }
}