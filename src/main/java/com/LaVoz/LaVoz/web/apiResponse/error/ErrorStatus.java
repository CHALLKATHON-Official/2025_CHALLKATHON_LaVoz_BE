package com.LaVoz.LaVoz.web.apiResponse.error;

import com.LaVoz.LaVoz.web.apiResponse.StatusCode;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorStatus {

    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, StatusCode.COMMON.getCode(500), "서버 에러, 관리자에게 문의 바랍니다."),
    _GATEWAY_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, StatusCode.COMMON.getCode(504), "서버 에러, 관리자에게 문의 바랍니다."),

    _BAD_REQUEST(HttpStatus.BAD_REQUEST, StatusCode.COMMON.getCode(400), "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, StatusCode.COMMON.getCode(401), "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, StatusCode.COMMON.getCode(403), "금지된 요청입니다."),
    _NOT_FOUND(HttpStatus.NOT_FOUND, StatusCode.COMMON.getCode(404), "찾을 수 없는 요청입니다."),
    _LOGIN_FAILURE(HttpStatus.UNAUTHORIZED, StatusCode.COMMON.getCode(405), "로그인을 실패했습니다."),

    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4001), "존재하지 않는 유저입니다."),
    EMAIL_NOT_EXIST(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4002), "이메일은 필수입니다."),
    EMAIL_INVALID(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4003), "잘못된 이메일 형식입니다."),
    LOGIN_ID_DUPLICATED(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4004), "중복된 아이디입니다."),
    PASSWORD_NOT_EXIST(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4005), "비밀번호는 필수입니다."),
    PASSWORD_INVALID(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4006), "잘못된 비밀번호 형식입니다."),
    PASSWORD_WRONG(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4007), "잘못된 비밀번호입니다."),
    NICKNAME_INVALID(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4008), "잘못된 닉네임 형식입니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4009), "닉네임은 필수입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, StatusCode.USER.getCode(4010), "존재하지 않는 리프레쉬 토큰입니다."),
    TOKEN_INVALID(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4011), "로그인이 필요합니다."),
    TOKEN_EXPIRATION(HttpStatus.BAD_REQUEST, StatusCode.USER.getCode(4012), "만료된 토큰입니다."),
    COOKIE_EMPTY(HttpStatus.BAD_REQUEST, StatusCode.COMMON.getCode(4016), "쿠키가 비어있습니다."),
    MEMBER_ORGANIZATION_NOT_FOUND(HttpStatus.NOT_FOUND, StatusCode.USER.getCode(4017), "조직의 멤버가 아닙니다."),

    ORGANIZATION_NOT_FOUND(HttpStatus.NOT_FOUND, StatusCode.ORGANIZATION.getCode(5001), "조직을 찾을 수 없습니다."),

    NOTE_NOT_FOUND(HttpStatus.NOT_FOUND, StatusCode.NOTE.getCode(6001), "노트가 없습니다.")
    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public ErrorResponse getReason(){
        return ErrorResponse.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }
}