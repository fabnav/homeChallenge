package com.homechallenge.pokedex.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {
    
    @Value("${pokeapi.base.url}")
    private String pokeApiBaseUrl;
    
    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(pokeApiBaseUrl)
                .build();
    }
}

