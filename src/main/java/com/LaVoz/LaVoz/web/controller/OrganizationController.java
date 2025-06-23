package com.LaVoz.LaVoz.web.controller;

import com.LaVoz.LaVoz.common.security.CustomUserDetails;
import com.LaVoz.LaVoz.service.MemberService;
import com.LaVoz.LaVoz.service.OrganizationService;
import com.LaVoz.LaVoz.web.apiResponse.ApiResponse;
import com.LaVoz.LaVoz.web.apiResponse.success.SuccessStatus;
import com.LaVoz.LaVoz.web.dto.response.OrganizationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/organization")
@RequiredArgsConstructor
public class OrganizationController {
    private final MemberService memberService;
    private final OrganizationService organizationService;
    
    /**
     * Organization 생성
     */
    @PostMapping("/new")
    public ApiResponse<OrganizationResponse> createPetition(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody String organizationName
    ) {
        return ApiResponse.onSuccess(
                SuccessStatus.ORGANIZATION_CREATED_SUCCESS,
                organizationService.createOrganization(
                     organizationName,
                     customUserDetails.getMember().getMemberId()   
                ));
    }
    
    /**
     * 현재 로그인한 사용자가 속한 모든 Organization 목록 조회
     */
    @GetMapping
    public ApiResponse<List<OrganizationResponse>> getOrganizations(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        List<OrganizationResponse> organizations = organizationService.findOrganizationsByMemberId(
                customUserDetails.getMember().getMemberId()
        );
        
        return ApiResponse.onSuccess(
                SuccessStatus.GET_ORGANIZATION_LIST_SUCCESS,
                organizations
        );
    }
    
    /**
     * Organization 삭제
     * 현재 로그인한 사용자가 속한 Organization만 삭제 가능
     */
    @DeleteMapping("/{organizationId}")
    public ApiResponse<Boolean> deleteOrganization(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long organizationId
    ) {
        boolean result = organizationService.deleteOrganization(
                organizationId,
                customUserDetails.getMember().getMemberId()
        );
        
        return ApiResponse.onSuccess(
                SuccessStatus.ORGANIZATION_DELETED_SUCCESS,
                result
        );
    }

    /**
     * 조직에 새로운 멤버 추가
     * 현재 로그인한 사용자가 속한 조직에만 멤버 추가 가능
     */
    @PostMapping("/{organizationId}/members/{memberId}")
    public ApiResponse<Boolean> addMemberToOrganization(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long organizationId,
            @PathVariable Long memberId
    ) {
        boolean result = organizationService.addMemberToOrganization(
                organizationId,
                memberId,
                customUserDetails.getMember().getMemberId()
        );
    
        return ApiResponse.onSuccess(
                SuccessStatus.MEMBER_ADDED_TO_ORGANIZATION_SUCCESS,
                result
        );
    }
}