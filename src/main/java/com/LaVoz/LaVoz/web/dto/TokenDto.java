package com.LaVoz.LaVoz.web.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class TokenDto {
    private String refreshToken;
    private String accessToken;
}

