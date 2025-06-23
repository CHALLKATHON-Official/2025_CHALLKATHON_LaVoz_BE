package com.LaVoz.LaVoz.web.controller;


import com.LaVoz.LaVoz.common.openai.dto.ChatGptResponse;
import com.LaVoz.LaVoz.common.security.CustomUserDetails;
import com.LaVoz.LaVoz.service.ChatGptService;
import com.LaVoz.LaVoz.web.apiResponse.ApiResponse;
import com.LaVoz.LaVoz.web.apiResponse.success.SuccessStatus;
import com.LaVoz.LaVoz.web.dto.response.ChildStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/organization")
@RequiredArgsConstructor
@Slf4j
public class OrganizationController {

    private final ChatGptService chatGptService;

    @PostMapping("/{organization_id}/state-analysis")
    public ApiResponse<ChildStatusResponse> stateAnalysis(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("organization_id") Long organizationId
    ) throws IOException {
        ChildStatusResponse childStatusResponse = chatGptService.analyzeChildState(organizationId, customUserDetails.getMember());
        return ApiResponse.onSuccess(SuccessStatus.STATE_ANALYSIS_SUCCESS, childStatusResponse);
    }
}
