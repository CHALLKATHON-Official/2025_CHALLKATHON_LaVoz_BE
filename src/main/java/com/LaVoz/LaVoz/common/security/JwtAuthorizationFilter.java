package com.LaVoz.LaVoz.common.security;

import com.LaVoz.LaVoz.common.exception.AuthenticationException;
import com.LaVoz.LaVoz.domain.Member;
import com.LaVoz.LaVoz.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.LaVoz.LaVoz.web.apiResponse.error.ErrorStatus;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Builder
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AuthenticationException authenticationException;
    private final MemberRepository memberRepository;

    @Override

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            //토큰 추출
            String token = jwtUtil.resolveAccessToken(request);
            if (token == null) {
                log.info("[Authorization] 접근 토큰 없음 - URI: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            //토큰 유효기간 확인
            if (jwtUtil.isExpired(token)) {
                log.warn("[Authorization] 토큰 만료됨 - URI: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            String username = jwtUtil.getUsername(token);
            String role = jwtUtil.getRole(token);
            log.info("[Authorization] 인증 시작 - 아이디: {}, 역할: {}", username, role);

            SecurityContextHolder.clearContext();
            Optional<Member> member = memberRepository.findMemberByLoginId(username);
            CustomUserDetails customUserDetails = CustomUserDetails.from(member.get());
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.info("[Authorization] 사용자 인증 완료 - userId: {}, URI: {}", member.get().getMemberId(), request.getRequestURI());

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            log.warn("[Authorization] JWT 만료 예외 발생 - {}", e.getMessage());
            SecurityContextHolder.clearContext();
            authenticationException.sendErrorResponse(response, ErrorStatus.TOKEN_EXPIRATION);
        } catch (Exception e) {
            log.error("[Authorization] jwt 만료도, 블랙리스트 토큰도 아닌 인증 실패 - {}", e.getMessage());
            SecurityContextHolder.clearContext();
            authenticationException.sendErrorResponse(response, ErrorStatus.TOKEN_INVALID);
        }
    }
}
