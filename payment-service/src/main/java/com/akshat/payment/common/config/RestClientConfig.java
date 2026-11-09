package com.akshat.payment.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient couponRestClient(){

        return RestClient.builder()
                .baseUrl("http://localhost:8086")
                .build();
    }

    @Bean
    public RestClient orderRestClient(){

        return RestClient.builder()
                .baseUrl("http://localhost:8085")
                .build();
    }
}
