package com.LaVoz.LaVoz.repository;

import com.LaVoz.LaVoz.domain.Member;
import com.LaVoz.LaVoz.domain.Organization;
import com.LaVoz.LaVoz.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status, Long> {

    Optional<Status> findTopByOrganizationOrderByCreatedAtDesc(Organization organization);
}
