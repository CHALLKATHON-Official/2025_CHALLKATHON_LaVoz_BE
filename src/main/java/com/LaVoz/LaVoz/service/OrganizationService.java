package com.LaVoz.LaVoz.service;

import com.LaVoz.LaVoz.domain.Member;
import com.LaVoz.LaVoz.domain.Organization;
import com.LaVoz.LaVoz.domain.MemberOrganization;
import com.LaVoz.LaVoz.repository.MemberOrganizationRepository;
import com.LaVoz.LaVoz.repository.MemberRepository;
import com.LaVoz.LaVoz.repository.OrganizationRepository;
import com.LaVoz.LaVoz.web.dto.response.OrganizationResponseDto;
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
    
    /**
     * 특정 멤버가 속한 모든 Organization 목록을 DTO로 변환하여 반환
     */
    public List<OrganizationResponseDto> findOrganizationsByMemberId(Long memberId) {
        List<MemberOrganization> memberOrganizations = 
            memberOrganizationRepository.findByMember_MemberId(memberId);
            
        return memberOrganizations.stream()
            .map(MemberOrganization::getOrganization)
            .map(OrganizationResponseDto::from)
            .collect(Collectors.toList());
    }
    
    /**
     * 새로운 Organization을 생성하고 생성한 멤버를 자동으로 추가
     */
    @Transactional
    public OrganizationResponseDto createOrganization(String name, Long memberId) {
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
        
        return OrganizationResponseDto.builder()
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
}