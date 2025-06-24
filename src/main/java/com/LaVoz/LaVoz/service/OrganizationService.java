package com.LaVoz.LaVoz.service;

import com.LaVoz.LaVoz.common.exception.ResourceNotFoundException;
import com.LaVoz.LaVoz.domain.Issue;
import com.LaVoz.LaVoz.domain.Member;
import com.LaVoz.LaVoz.domain.Organization;
import com.LaVoz.LaVoz.domain.MemberOrganization;
import com.LaVoz.LaVoz.repository.IssueRepository;
import com.LaVoz.LaVoz.repository.MemberOrganizationRepository;
import com.LaVoz.LaVoz.repository.MemberRepository;
import com.LaVoz.LaVoz.repository.OrganizationRepository;
import com.LaVoz.LaVoz.web.apiResponse.error.ErrorStatus;
import com.LaVoz.LaVoz.web.dto.response.IssueResponse;
import com.LaVoz.LaVoz.web.dto.response.OrganizationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationService {
    
    private final MemberOrganizationRepository memberOrganizationRepository;
    private final OrganizationRepository organizationRepository;
    private final MemberRepository memberRepository;
    private final IssueRepository issueRepository;
    
    /**
     * 특정 멤버가 속한 모든 Organization 목록을 DTO로 변환하여 반환
     */
    public List<OrganizationResponse> findOrganizationsByMemberId(Long memberId) {
        List<MemberOrganization> memberOrganizations = 
            memberOrganizationRepository.findByMember_MemberId(memberId);
            
        return memberOrganizations.stream()
            .map(MemberOrganization::getOrganization)
            .map(OrganizationResponse::from)
            .collect(Collectors.toList());
    }
    
    /**
     * 새로운 Organization을 생성하고 생성한 멤버를 자동으로 추가
     */
    @Transactional
    public OrganizationResponse createOrganization(String name, Long memberId) {
        // 멤버 조회
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));
        
        // Organization 생성 및 저장
        Organization organization = Organization.builder()
            .name(name)
            .build();
        Organization savedOrganization = organizationRepository.save(organization);
        
        // MemberOrganization 생성 및 저장 (멤버와 Organization 연결)
        MemberOrganization memberOrganization = MemberOrganization.builder()
            .member(member)
            .organization(savedOrganization)
            .build();
        memberOrganizationRepository.save(memberOrganization);
        
        return OrganizationResponse.builder()
            .organizationId(savedOrganization.getOrganizationId())
            .name(savedOrganization.getName())
            .build();
    }

/**
 * Organization을 삭제
 * 해당 Organization에 연결된 모든 관계(MemberOrganization, Status, Note)도 함께 삭제
 */
@Transactional
public boolean deleteOrganization(Long organizationId, Long memberId) {
    Organization organization = organizationRepository.findById(organizationId)
            .orElseThrow(() -> new IllegalArgumentException("Organization not found with id: " + organizationId));

    boolean isMemberInOrganization = memberOrganizationRepository
            .existsByMember_MemberIdAndOrganization_OrganizationId(memberId, organizationId);
    
    if (!isMemberInOrganization) {
        throw new IllegalArgumentException("Member with id " + memberId + 
                " does not belong to Organization with id " + organizationId);
    }

    organizationRepository.delete(organization);
    
    return true;
}

/**
 * 기존 Organization에 새로운 멤버를 추가
 */
@Transactional
public boolean addMemberToOrganization(Long organizationId, Long memberId, Long requesterId) {
    // 조직 존재 여부 확인
    Organization organization = organizationRepository.findById(organizationId)
            .orElseThrow(() -> new IllegalArgumentException("Organization not found with id: " + organizationId));
    
    // 요청자가 해당 조직에 속하는지 확인 (권한 검증)
    boolean isRequesterInOrganization = memberOrganizationRepository
            .existsByMember_MemberIdAndOrganization_OrganizationId(requesterId, organizationId);
    
    if (!isRequesterInOrganization) {
        throw new IllegalArgumentException("Requester with id " + requesterId + 
                " does not belong to Organization with id " + organizationId);
    }
    
    // 추가할 멤버 존재 여부 확인
    Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));
    
    // 이미 조직에 속한 멤버인지 확인
    boolean isMemberAlreadyInOrganization = memberOrganizationRepository
            .existsByMember_MemberIdAndOrganization_OrganizationId(memberId, organizationId);
    
    if (isMemberAlreadyInOrganization) {
        throw new IllegalArgumentException("Member with id " + memberId + 
                " already belongs to Organization with id " + organizationId);
    }
    
    // MemberOrganization 생성 및 저장 (멤버와 Organization 연결)
    MemberOrganization memberOrganization = MemberOrganization.builder()
            .member(member)
            .organization(organization)
            .build();
    
    memberOrganizationRepository.save(memberOrganization);
    
    return true;
}

    /**
     * 조직의 내 이슈 목록 조회 (최신순)
     */
    public List<IssueResponse> getMyIssuesByOrganization(Long organizationId, Member member) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorStatus.ORGANIZATION_NOT_FOUND));

        // 멤버 권한 확인
        boolean isOrganizationMember = memberOrganizationRepository.existsByMember_MemberIdAndOrganization_OrganizationId(member.getMemberId(), organizationId);
        if (!isOrganizationMember) {
            throw new ResourceNotFoundException(ErrorStatus.MEMBER_ORGANIZATION_NOT_FOUND);
        }

        // 해당 조직에서 내가 작성한 이슈만 조회
        List<Issue> myIssues = issueRepository.findByOrganizationAndMemberOrderByCreatedAtDesc(organization, member);

        return myIssues.stream()
                .map(issue -> IssueResponse.builder()
                        .question(issue.getQuestion())
                        .answer(issue.getAnswer())
                        .issueId(issue.getIssueId())
                        .memberId(issue.getMember().getMemberId())
                        .memberName(issue.getMember().getName())
                        .organizationId(issue.getOrganization().getOrganizationId())
                        .organizationName(issue.getOrganization().getName())
                        .createdAt(issue.getCreatedAt())
                        .updatedAt(issue.getUpdatedAt())
                        .build())
                .toList();
    }
}