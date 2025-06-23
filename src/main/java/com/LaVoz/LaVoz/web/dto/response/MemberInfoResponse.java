package com.LaVoz.LaVoz.web.dto.response;

import com.LaVoz.LaVoz.web.dto.TokenDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "로그인 응답 DTO")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberInfoResponse {

    private Long memberId;
    private String loginId;
    private String name;
    private String role;
    private String imageUrl;
    private TokenDto tokenDto;
}
