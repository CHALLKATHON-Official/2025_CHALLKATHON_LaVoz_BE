package com.LaVoz.LaVoz.common.security;

import com.LaVoz.LaVoz.common.Constant;
import com.LaVoz.LaVoz.domain.Member;
import com.LaVoz.LaVoz.service.TokenService;
import com.LaVoz.LaVoz.web.apiResponse.success.SuccessStatus;
import com.LaVoz.LaVoz.web.dto.TokenDto;
import com.LaVoz.LaVoz.web.dto.request.LoginRequest;
import com.LaVoz.LaVoz.web.dto.response.MemberInfoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.LaVoz.LaVoz.web.apiResponse.ApiResponse;
import com.LaVoz.LaVoz.web.apiResponse.error.ErrorStatus;
import com.LaVoz.LaVoz.common.exception.AuthenticationException;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtUserLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final TokenService tokenService;
    @Value("${pepper.secret}") private String pepperSecret;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws org.springframework.security.core.AuthenticationException {
        try {
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(),
                    LoginRequest.class);
            log.info("[Authentication] 사용자 로그인 필터 - 이메일: {}", loginRequest.getLoginId());

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getLoginId(), loginRequest.getPassword()+pepperSecret, null);

            //authenticationManager가 이메일, 비밀번호로 검증을 진행
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            log.error("[Authentication] 로그인 요청 파싱 실패: {}", e.getMessage());
            throw new RuntimeException("Failed to parse authentication request body", e);
        }
    }

    //로그인 성공시 실행하는 메소드
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException{

        CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
        Member member = customUserDetails.getMember();
        log.info("[Authentication] 사용자 로그인 성공 - memberId: {}, 아이디: {}", member.getMemberId(), member.getLoginId());

        // Access Token, Refresh Token 발급
        String accessToken = jwtUtil.createAccessToken(member.getLoginId(), member.getRole().name());
        String refreshToken = jwtUtil.createRefreshToken(member.getLoginId(), member.getRole().name());

        // Refresh Token 저장
        tokenService.saveRefreshToken(member, refreshToken);

        // Refresh Token을 쿠키에 설정
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(Constant.REFRESH_COOKIE_EXPIRATION) // 3일
                .path("/")
                .build();

        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        TokenDto tokenDto = TokenDto.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .build();

        MemberInfoResponse memberInfoResponse = MemberInfoResponse.builder()
                .memberId(member.getMemberId())
                .loginId(member.getLoginId())
                .name(member.getName())
                .role("USER")
                .tokenDto(tokenDto)
                .build();

        // 응답 바디 작성
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.onSuccess(SuccessStatus.USER_LOGIN_SUCCESS, memberInfoResponse)));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException failed) throws IOException, ServletException {
        log.warn("[Authentication] 로그인 실패 - 사유: {}", failed.getMessage());
        AuthenticationException exception = new AuthenticationException(objectMapper);

        //로그인 실패 이유 알 수 없도록 일반적인 오류 메세지 보내기
        exception.sendErrorResponse(response, ErrorStatus._LOGIN_FAILURE);
    }
}
