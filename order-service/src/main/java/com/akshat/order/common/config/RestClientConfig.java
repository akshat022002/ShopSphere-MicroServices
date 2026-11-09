package com.akshat.order.common.config;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient catalogRestClient(){

        return  createRestClient("http://localhost:8081");

    }

    @Bean
    public RestClient cartRestClient(){

        return createRestClient("http://localhost:8083");
    }

    @Bean
    public RestClient couponRestClient(){

        return createRestClient("http://localhost:8086");
    }

    @Bean
    public RestClient inventoryRestClient(){

       return createRestClient("http://localhost:8082");

    }

    private RestClient createRestClient(String url){

        RequestConfig reqConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(2,TimeUnit.SECONDS)
                .setResponseTimeout(3,TimeUnit.SECONDS)
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(reqConfig)
                .build();

        return RestClient.builder()
                .baseUrl(url)
                .requestFactory(
                        new HttpComponentsClientHttpRequestFactory(httpClient)
                )
                .build();

    }
}
