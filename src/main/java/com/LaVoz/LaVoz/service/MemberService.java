package com.LaVoz.LaVoz.service;

import com.LaVoz.LaVoz.common.exception.ResourceNotFoundException;
import com.LaVoz.LaVoz.domain.Member;
import com.LaVoz.LaVoz.repository.MemberRepository;
import com.LaVoz.LaVoz.web.apiResponse.error.ErrorStatus;
import com.LaVoz.LaVoz.web.dto.request.LoginRequest;
import com.LaVoz.LaVoz.web.dto.request.MemberRegisterRequest;
import com.LaVoz.LaVoz.web.dto.response.MemberInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Value("${pepper.secret}") private String pepperSecret;

    /**
     * 회원가입
     */
    public MemberInfoResponse registerMember(MemberRegisterRequest request) throws IOException {
        String loginId = request.getLoginId();
        String password = request.getPassword();
        String name = request.getName();

        log.info("[회원가입 요청] 아이디: {}, 이름: {}, 역할: {}", loginId, name, request.getRole());

        // 중복된 아이디인지 확인
        Boolean isExistMember = memberRepository.existsByLoginId(loginId);
        if (isExistMember) {
            log.warn("[회원가입 실패] 중복된 아이디: {}", loginId);
            throw new ResourceNotFoundException(ErrorStatus.LOGIN_ID_DUPLICATED);
        }

        // 비밀번호 암호화
        String encodedPassword = bCryptPasswordEncoder.encode(password + pepperSecret);

        // 새로운 유저
        Member newMember = Member.builder()
                .loginId(loginId)
                .password(encodedPassword) // 이미 암호화된 비밀번호 사용
                .name(name)
                .role(request.getRole())
                .imageUrl(StringUtils.hasText(request.getImageUrl()) ?
                        request.getImageUrl() : null) // 기본 이미지 URL 처리
                .build();

        Member savedMember = memberRepository.save(newMember);

        log.info("[회원가입 성공] userId={}, loginId={}", savedMember.getMemberId(), savedMember.getLoginId());

        return MemberInfoResponse.builder()
                .memberId(savedMember.getMemberId())
                .loginId(savedMember.getLoginId())
                .name(savedMember.getName())
                .role(savedMember.getRole().name())
                .imageUrl(savedMember.getImageUrl())
                .build();
    }

    /**
     * 로그인 ID 중복 체크
     */
    @Transactional(readOnly = true)
    public boolean isLoginIdAvailable(String loginId) {
        return !memberRepository.existsByLoginId(loginId);
    }
}
