package com.LaVoz.LaVoz.web.controller;

import com.LaVoz.LaVoz.common.Constant;
import com.LaVoz.LaVoz.common.exception.ResourceNotFoundException;
import com.LaVoz.LaVoz.service.MemberService;
import com.LaVoz.LaVoz.service.TokenService;
import com.LaVoz.LaVoz.web.apiResponse.ApiResponse;
import com.LaVoz.LaVoz.web.apiResponse.error.ErrorStatus;
import com.LaVoz.LaVoz.web.apiResponse.success.SuccessStatus;
import com.LaVoz.LaVoz.web.dto.request.LoginRequest;
import com.LaVoz.LaVoz.web.dto.request.MemberRegisterRequest;
import com.LaVoz.LaVoz.web.dto.response.MemberInfoResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final TokenService tokenService;

    /**
     * 회원가입
     */
    @PostMapping("/register")
    public ApiResponse<MemberInfoResponse> register(
            @Valid @RequestBody MemberRegisterRequest request) throws IOException {

        MemberInfoResponse response = memberService.registerMember(request);

        return ApiResponse.onSuccess(SuccessStatus.SIGN_IN_SUCCESS, response);
    }

    /**
     * 로그인 ID 중복 체크
     */
    @GetMapping("/check-duplicated-loginId")
    public ResponseEntity<Boolean> checkLoginIdAvailability(
            @RequestParam("loginId") String loginId) {

        boolean isAvailable = memberService.isLoginIdAvailable(loginId);
        return ResponseEntity.ok(!isAvailable);
    }

    //스웨거 문서화 용 (스웨거에서 /member/login으로 요청을 보내도 컨트롤러로 들어오지 않고 jwtUserLoginFilter가 가로채서 로그인을 진행합니다.)
    @PostMapping("/login")
    public ApiResponse<MemberInfoResponse> loginAdmin(@RequestBody LoginRequest request){
        return ApiResponse.onSuccess(SuccessStatus.USER_LOGIN_SUCCESS, null);
    }

    /**
     * 토큰 재발급
     */
    @PostMapping("/reissue")
    public ApiResponse<MemberInfoResponse> reissueUserToken(
            @CookieValue(name = "refresh_token") String refreshToken, HttpServletResponse response) throws IOException{

        if (refreshToken == null) {
            throw new ResourceNotFoundException(ErrorStatus.COOKIE_EMPTY);
        }

        MemberInfoResponse reissueResponse = tokenService.reissueToken(refreshToken);

        // Refresh Token을 쿠키에 설정
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", reissueResponse.getTokenDto().getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(Constant.REFRESH_COOKIE_EXPIRATION) // 14일(7 * 24 * 60 * 60)
                .path("/")
                .build();
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        return ApiResponse.onSuccess(SuccessStatus.REISSUE_TOKEN_SUCCESS, reissueResponse);
    }

    @GetMapping("/test")
    public ResponseEntity<String> testAuth(){
        return ResponseEntity.ok("Hello");
    }
}