package com.LaVoz.LaVoz.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardCreateRequest {

    @NotBlank(message = "게시글 제목은 필수입니다")
    private String title;
    @NotBlank(message = "게시글 내용은 필수입니다")
    private String content;
}
