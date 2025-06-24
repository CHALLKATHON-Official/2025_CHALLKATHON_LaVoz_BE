package com.LaVoz.LaVoz.common.security;

import com.LaVoz.LaVoz.domain.Member;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Builder
public class CustomUserDetails implements UserDetails {

    private final Member member;
    private final String name;
    private final String loginId;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public static CustomUserDetails from(Member member) {

        return CustomUserDetails.builder()
                .member(member)
                .name(member.getName())
                .loginId(member.getLoginId())
                .password(member.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(member.getRole().name())))
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return authorities;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}

