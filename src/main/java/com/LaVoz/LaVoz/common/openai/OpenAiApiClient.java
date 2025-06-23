package com.LaVoz.LaVoz.common.openai;

import com.LaVoz.LaVoz.common.openai.dto.ChatGptRequest;
import com.LaVoz.LaVoz.common.openai.dto.ChatGptResponse;
import com.LaVoz.LaVoz.common.openai.dto.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * WebClient를 사용하여 OpenAi Api와 통신. OpenAi Api와 통신하기 위한 필수적인 헤더들 추가하는 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiApiClient {

    private final WebClient webClient;

    public ChatGptResponse sendRequestToModel(String model, List<Message> messages, int maxTokens, double temperature) {
        ChatGptRequest request = new ChatGptRequest(model, messages, maxTokens, temperature);
        log.info("request: {}", request);

        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatGptResponse.class)
                .block();
    }
}