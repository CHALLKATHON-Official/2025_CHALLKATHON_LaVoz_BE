package com.LaVoz.LaVoz.service;

import com.LaVoz.LaVoz.domain.MemberOrganization;
import com.LaVoz.LaVoz.repository.MemberOrganizationRepository;
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
    
    /**
     * 특정 멤버가 속한 모든 Organization 목록 반환
     */
    public List<OrganizationResponseDto> findOrganizationsByMemberId(Long memberId) {
        List<MemberOrganization> memberOrganizations = 
            memberOrganizationRepository.findByMember_MemberId(memberId);
            
        return memberOrganizations.stream()
            .map(MemberOrganization::getOrganization)
            .map(OrganizationResponseDto::from)
            .collect(Collectors.toList());
    }
}