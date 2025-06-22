package com.LaVoz.LaVoz.service;

import com.LaVoz.LaVoz.common.exception.ResourceNotFoundException;
import com.LaVoz.LaVoz.common.security.JwtUtil;
import com.LaVoz.LaVoz.domain.Member;
import com.LaVoz.LaVoz.domain.RefreshToken;
import com.LaVoz.LaVoz.repository.RefreshTokenRepository;
import com.LaVoz.LaVoz.web.apiResponse.error.ErrorStatus;
import com.LaVoz.LaVoz.web.dto.TokenDto;
import com.LaVoz.LaVoz.web.dto.response.MemberInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    public void saveRefreshToken(Member member, String refreshToken) {

        RefreshToken refreshTokenEntity = refreshTokenRepository.findByMember(member)
                .orElse(RefreshToken.builder()
                        .member(member)
                        .loginId(member.getLoginId())
                        .token(refreshToken)
                        .build());

        refreshTokenEntity.updateToken(refreshToken);
        refreshTokenRepository.save(refreshTokenEntity);
    }

    public MemberInfoResponse reissueToken(String oldRefreshToken) {
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(oldRefreshToken)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorStatus.REFRESH_TOKEN_NOT_FOUND));

        Member member = refreshTokenEntity.getMember();
        String role = member.getRole().name();
        String loginId = member.getLoginId();
        String name = member.getName();

        // 새로운 access token & refresh token 발급
        String newAccessToken = jwtUtil.createAccessToken(loginId, role);
        String newRefreshToken = jwtUtil.createRefreshToken(loginId, role);

        // refresh token 교체 (영속성 컨텍스트에서 관리)
        refreshTokenEntity.updateToken(newRefreshToken);

        TokenDto tokenDto = TokenDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
        return MemberInfoResponse.builder()
                .memberId(refreshTokenEntity.getId())
                .loginId(loginId)
                .name(name)
                .role(role)
                .tokenDto(tokenDto)
                .build();
    }
}

