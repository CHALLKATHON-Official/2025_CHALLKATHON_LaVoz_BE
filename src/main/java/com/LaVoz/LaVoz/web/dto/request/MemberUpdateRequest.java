package com.LaVoz.LaVoz.web.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberUpdateRequest {

    private String name;

    private String imageUrl;

    private String childName;

    private String childGender;

    private String childBirthday;

    private String childImageUrl;
}
