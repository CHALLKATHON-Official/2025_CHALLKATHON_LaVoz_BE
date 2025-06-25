package com.LaVoz.LaVoz.web.controller;


import com.LaVoz.LaVoz.common.security.CustomUserDetails;
import com.LaVoz.LaVoz.service.MemberService;
import com.LaVoz.LaVoz.service.OrganizationService;
import com.LaVoz.LaVoz.service.ChatGptService;
import com.LaVoz.LaVoz.web.apiResponse.ApiResponse;
import com.LaVoz.LaVoz.web.apiResponse.success.SuccessStatus;
import com.LaVoz.LaVoz.web.dto.request.IssueRequest;
import com.LaVoz.LaVoz.web.dto.request.JoinOrganizationRequest;
import com.LaVoz.LaVoz.web.dto.request.OrganizationCreateRequest;
import com.LaVoz.LaVoz.web.dto.response.ChildStatusResponse;
import com.LaVoz.LaVoz.web.dto.response.IssueResponse;
import com.LaVoz.LaVoz.web.dto.response.OrganizationResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/organization")
@RequiredArgsConstructor
@Slf4j
public class OrganizationController {

    private final OrganizationService organizationService;
    private final ChatGptService chatGptService;

    /**
     * Organization 생성
     */
    @PostMapping("/new")
    public ApiResponse<OrganizationResponse> createPetition(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody OrganizationCreateRequest organizationCreateRequest
            ) {
        return ApiResponse.onSuccess(
                SuccessStatus.ORGANIZATION_CREATED_SUCCESS,
                organizationService.createOrganization(
                        organizationCreateRequest.getOrganizationName(),
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

    @Operation(summary = "상태 분석",
            description = """
                    행동 노트 기반 gpt 분석
                    분석 항목:
                    1. 시간별 감정과 주된 행동
                    2. 감정별 반복 행동
                    3. 감각 자극에 대한 민감 반응 횟수 누적
                    """)
    @PostMapping("/{organization_id}/state-analysis")
    public ApiResponse<ChildStatusResponse> stateAnalysis(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("organization_id") Long organizationId
    ) throws IOException {
        ChildStatusResponse childStatusResponse = chatGptService.analyzeChildState(organizationId, customUserDetails.getMember());
        return ApiResponse.onSuccess(SuccessStatus.STATE_ANALYSIS_SUCCESS, childStatusResponse);
    }

    @PostMapping("/{organization_id}/issue")
    public ApiResponse<IssueResponse> issue(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("organization_id") Long organizationId,
            @RequestBody IssueRequest issueRequest
    ) throws IOException {
        IssueResponse response = chatGptService.issue(issueRequest, organizationId, customUserDetails.getMember());
        return ApiResponse.onSuccess(SuccessStatus.ISSUE_ANSWER_SUCCESS, response);
    }

    /**
     * 내가 질문한 이슈들 조회
     */
    @GetMapping("/{organization_id}/issues")
    public ApiResponse<List<IssueResponse>> getMyIssuesByOrganization(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("organization_id") Long organizationId
    ) {
        List<IssueResponse> responses = organizationService.getMyIssuesByOrganization(
                organizationId,
                customUserDetails.getMember()
        );

        return ApiResponse.onSuccess(SuccessStatus.GET_MY_ORGANIZATION_ISSUES_SUCCESS, responses);
    }

    /**
     * 인증 코드를 사용하여 Organization에 가입
     */
    @PostMapping("/join")
    public ApiResponse<OrganizationResponse> joinOrganizationByInviteCode(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody JoinOrganizationRequest joinRequest
    ) {
        OrganizationResponse response = organizationService.joinOrganizationByInviteCode(
                joinRequest.getInviteCode(),
                customUserDetails.getMember().getMemberId()
        );

        return ApiResponse.onSuccess(
                SuccessStatus.MEMBER_ADDED_TO_ORGANIZATION_SUCCESS,
                response
        );
    }

}