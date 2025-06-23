package com.LaVoz.LaVoz.common.openai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ChatGptResponse {
    private List<Choice> choices;

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Choice {
        private int index;
        private Message message;
    }
}