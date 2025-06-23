package com.LaVoz.LaVoz.web.dto.request;

import com.LaVoz.LaVoz.domain.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberRegisterRequest {

    @NotBlank(message = "로그인 ID는 필수입니다")
    @Size(min = 4, max = 20, message = "로그인 ID는 4-20자 사이여야 합니다")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, max = 20, message = "비밀번호는 8-20자 사이여야 합니다")
    private String password;

    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 50, message = "이름은 50자를 초과할 수 없습니다")
    private String name;

    @NotNull(message = "역할은 필수입니다")
    private Role role;

    private String imageUrl; // 선택사항
}