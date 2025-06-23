package com.LaVoz.LaVoz.repository;


import com.LaVoz.LaVoz.domain.Organization;
import com.LaVoz.LaVoz.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Organization findByOrganizationId(Long organizationId);
}
