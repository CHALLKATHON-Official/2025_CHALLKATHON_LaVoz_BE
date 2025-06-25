package com.LaVoz.LaVoz.repository;

import com.LaVoz.LaVoz.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Organization findByOrganizationId(Long organizationId);

    Optional<Organization> findByInviteCode(String inviteCode);

    boolean existsByInviteCode(String inviteCode);

}
