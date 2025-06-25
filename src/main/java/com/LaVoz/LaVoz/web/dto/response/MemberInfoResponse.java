package com.LaVoz.LaVoz.web.dto.response;

import com.LaVoz.LaVoz.domain.Member;
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

    public static MemberInfoResponse from(Member member) {
        return MemberInfoResponse.builder()
                .memberId(member.getMemberId())
                .loginId(member.getLoginId())
                .name(member.getName())
                .role(member.getRole().name())
                .imageUrl(member.getImageUrl())
                .build();
    }

    public static MemberInfoResponse from(Member member, TokenDto tokenDto) {
        return MemberInfoResponse.builder()
                .memberId(member.getMemberId())
                .loginId(member.getLoginId())
                .name(member.getName())
                .role(member.getRole().name())
                .imageUrl(member.getImageUrl())
                .tokenDto(tokenDto)
                .build();
    }
}
