package com.LaVoz.LaVoz.common.openai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ChatGptRequest {
    private String model;
    private List<Message> messages;
    private int max_tokens;
    private double temperature;
}
