package com.LaVoz.LaVoz.common.openai.templates;

/**
 * OpenAi Api와 통신하기 위한 프롬프트 템플릿
 */
public class PromptTemplate {

    private static final String BASE_TEMPLATE = """
            ### 요청하고 싶은 것
            {{request}}
            
            ### 응답 값 형식
            {{responseFormat}}
            """;

    private final String template;

    public PromptTemplate() {
        this.template = BASE_TEMPLATE;
    }

    public String fillTemplate(String request, String responseFormat) {
        return template.replace("{{request}}", request)
                .replace("{{responseFormat}}", responseFormat);
    }
}
