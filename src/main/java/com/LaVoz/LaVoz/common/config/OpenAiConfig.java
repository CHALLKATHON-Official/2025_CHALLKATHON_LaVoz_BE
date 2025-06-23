package com.LaVoz.LaVoz.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * secret key와 url에 맞게 WebClient를 빈으로 등록
 */
@Configuration
public class OpenAiConfig {

    @Value("${openai.api-key}")
    private String secretKey;

    // OpenAI API의 기본 URL
    private static final String OPENAI_BASE_URL = "https://api.openai.com/v1";

    // JSON 형태의 메타데이터와 secretKey 값을 넣은 공통 헤더
    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(OPENAI_BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + secretKey) // jwt 토큰으로 Bearer 토큰 값을 입력하여 전송
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
