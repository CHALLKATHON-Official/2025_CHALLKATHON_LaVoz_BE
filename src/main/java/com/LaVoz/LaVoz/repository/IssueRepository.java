package com.LaVoz.LaVoz.repository;

import com.LaVoz.LaVoz.domain.Issue;
import com.LaVoz.LaVoz.domain.Member;
import com.LaVoz.LaVoz.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    List<Issue> findByOrganizationAndMemberOrderByCreatedAtDesc(Organization organization, Member member);
}
