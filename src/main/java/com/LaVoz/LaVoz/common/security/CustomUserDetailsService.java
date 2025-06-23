package com.LaVoz.LaVoz.common.security;

import com.LaVoz.LaVoz.common.exception.ResourceNotFoundException;
import com.LaVoz.LaVoz.domain.Member;
import com.LaVoz.LaVoz.repository.MemberRepository;
import com.LaVoz.LaVoz.web.apiResponse.error.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    //로그인된 사용자의 정보를 CustomUserDetails에 담아서 반환하는 역할
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("[Authentication] 사용자 CustomUserDetailsService - 입력 이메일: {}",username);

        //디비에서 사용자 정보 가져오기
        Member member = memberRepository.findMemberByLoginId(username)
                .orElseThrow(() -> {
                    log.warn("[Authentication] 사용자 정보 없음 - 로그인 아이디: {}", username);
                    return new ResourceNotFoundException(ErrorStatus.USER_NOT_FOUND);
                });

        return CustomUserDetails.from(member);
    }
}