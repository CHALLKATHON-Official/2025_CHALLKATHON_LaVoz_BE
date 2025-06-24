package com.LaVoz.LaVoz.web.apiResponse.success;

import com.LaVoz.LaVoz.web.apiResponse.StatusCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessStatus {

    _OK(HttpStatus.OK, StatusCode.COMMON.getCode(200), "요청이 성공적으로 처리되었습니다."),
    _CREATED(HttpStatus.CREATED, StatusCode.COMMON.getCode(201), "요청이 성공적으로 생성되었습니다."),

    SIGN_IN_SUCCESS(HttpStatus.OK, StatusCode.USER.getCode(2011), "성공적으로 회원가입되었습니다."),
    USER_LOGOUT_SUCCESS(HttpStatus.OK, StatusCode.USER.getCode(2001), "성공적으로 로그아웃되었습니다."),
    USER_EDIT_SUCCESS(HttpStatus.OK, StatusCode.USER.getCode(2002), "유저 정보가 성공적으로 변경되었습니다."),
    USER_SIGN_OUT_SUCCESS(HttpStatus.OK, StatusCode.USER.getCode(2003), "성공적으로 탈퇴되었습니다."),
    REISSUE_TOKEN_SUCCESS(HttpStatus.OK, StatusCode.USER.getCode(2003), "토큰이 성공적으로 재발급되었습니다."),
    USER_INFO_RETRIEVED(HttpStatus.OK, StatusCode.USER.getCode(2004), "유저 정보가 조회되었습니다."),
    USER_LOGIN_SUCCESS(HttpStatus.OK, StatusCode.USER.getCode(2005), "성공적으로 로그인되었습니다."),
    USER_PASSWORD_UPDATE_SUCCESS(HttpStatus.OK, StatusCode.USER.getCode(2006), "성공적으로 비밀번호가 변경되었습니다."),

    STATE_ANALYSIS_SUCCESS(HttpStatus.OK, StatusCode.STATE.getCode(4001), "성공적으로 아이 상태 분석을 생성 혹은 갱신하였습니다."),

    ISSUE_ANSWER_SUCCESS(HttpStatus.OK, StatusCode.ISSUE.getCode(5001), "이슈에 대한 답변이 성공적으로 생성되었습니다."),
    GET_MY_ORGANIZATION_ISSUES_SUCCESS(HttpStatus.OK, StatusCode.ISSUE.getCode(5002), "나의 조직에 대한 질문들을 성공적으로 조회하였습니다."),

    ORGANIZATION_CREATED_SUCCESS(HttpStatus.OK, StatusCode.ORGANIZATION.getCode(2011), "Organization이 성공적으로 생성되었습니다."),
    GET_ORGANIZATION_LIST_SUCCESS(HttpStatus.OK,StatusCode.ORGANIZATION.getCode(2001), "Organization이 성공적으로 반환되었습니다."),
    ORGANIZATION_DELETED_SUCCESS(HttpStatus.OK,StatusCode.ORGANIZATION.getCode(2012), "Organization이 성공적으로 삭제되었습니다."),
    MEMBER_ADDED_TO_ORGANIZATION_SUCCESS(HttpStatus.OK,StatusCode.ORGANIZATION.getCode(2012), "Organization에 Member가 성공적으로 추가되었습니다."),

    NOTE_CREATED_SUCCESS(HttpStatus.OK, StatusCode.NOTE.getCode(2011), "Note가 성공적으로 생성되었습니다."),
    GET_ORGANIZATION_NOTES_SUCCESS(HttpStatus.OK,StatusCode.NOTE.getCode(2001), "Note가 성공적으로 반환되었습니다."),
    GET_NOTE_DETAIL_SUCCESS(HttpStatus.OK,StatusCode.NOTE.getCode(2002), "Note의 상세정보가 성공적으로 반환되었습니다."),
    NOTE_DELETED_SUCCESS(HttpStatus.OK,StatusCode.NOTE.getCode(2012), "Note가 성공적으로 삭제되었습니다.")
    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}