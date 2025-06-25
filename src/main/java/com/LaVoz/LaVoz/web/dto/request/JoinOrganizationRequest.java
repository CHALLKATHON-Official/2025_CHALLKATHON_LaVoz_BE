package com.LaVoz.LaVoz.web.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinOrganizationRequest {
    @NotEmpty(message = "초대 코드는 필수입니다")
    @Size(min = 5, max = 5, message = "초대 코드는 5자리여야 합니다")
    private String inviteCode;
}
