package com.LaVoz.LaVoz.web.dto.response;

import com.LaVoz.LaVoz.domain.Member;
import com.LaVoz.LaVoz.domain.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MyPageResponse {
    private LocalDateTime memberCreationDate;
    private LocalDateTime memberUpdateDate;
    private Long memberId;
    private String memberName;
    private String loginId;
    private Role role;
    private String imageUrl;
    private String childName;
    private String childBirthday;
    private String childGender;
    private String childImageUrl;

    public static MyPageResponse from(Member member) {
        return MyPageResponse.builder()
                .memberCreationDate(member.getCreatedAt())
                .memberUpdateDate(member.getUpdatedAt())
                .memberId(member.getMemberId())
                .memberName(member.getName())
                .loginId(member.getLoginId())
                .role(member.getRole())
                .imageUrl(member.getImageUrl())
                .childName(member.getChildName())
                .childBirthday(member.getChildBirthday())
                .childGender(member.getChildGender())
                .childImageUrl(member.getChildImageUrl())
                .build();
    }
}
