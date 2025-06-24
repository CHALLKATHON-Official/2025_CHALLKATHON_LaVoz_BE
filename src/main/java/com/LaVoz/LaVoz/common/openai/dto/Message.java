package com.LaVoz.LaVoz.common.openai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Message {
    private String role; // "system", "user", "assistant"
    private String content;
}

