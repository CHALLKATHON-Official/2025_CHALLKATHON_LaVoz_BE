package com.LaVoz.LaVoz.repository;

import com.LaVoz.LaVoz.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByLoginId(String loginId);

    Optional<Member> findMemberByLoginId(String loginId);

    Optional<Member> findMemberByName(String name);
}
