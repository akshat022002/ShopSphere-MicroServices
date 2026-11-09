package com.akshat.review.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient orderRestClient(){

        return RestClient.builder()
                .baseUrl("http://localhost:8085")
                .build();
    }

    @Bean
    public RestClient catalogRestClient(){

        return RestClient.builder()
                .baseUrl("http://localhost:8081")
                .build();
    }
}
