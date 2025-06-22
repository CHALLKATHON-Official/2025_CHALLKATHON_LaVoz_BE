package com.LaVoz.LaVoz.repository;

import com.LaVoz.LaVoz.domain.Member;
import com.LaVoz.LaVoz.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByMember(Member member);

    Optional<RefreshToken> findByToken(String refreshToken);

    long deleteByLoginId(String LoginId);
}
