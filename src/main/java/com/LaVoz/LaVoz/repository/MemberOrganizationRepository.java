package com.LaVoz.LaVoz.repository;

import com.LaVoz.LaVoz.domain.MemberOrganization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberOrganizationRepository extends JpaRepository<MemberOrganization, Long> {
    List<MemberOrganization> findByMember_MemberId(Long memberId);
}
