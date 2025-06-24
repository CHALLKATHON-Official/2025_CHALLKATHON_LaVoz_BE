package com.LaVoz.LaVoz.web.controller;

import com.LaVoz.LaVoz.common.security.CustomUserDetails;
import com.LaVoz.LaVoz.service.NoteService;
import com.LaVoz.LaVoz.web.apiResponse.ApiResponse;
import com.LaVoz.LaVoz.web.apiResponse.success.SuccessStatus;
import com.LaVoz.LaVoz.web.dto.request.NoteCreateRequest;
import com.LaVoz.LaVoz.web.dto.response.NoteResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/organizations/{organizationId}/notes")
@RequiredArgsConstructor
public class NoteController {
    
    private final NoteService noteService;
    
    /**
     * 조직에 새로운 노트 생성
     */
    @PostMapping
    public ApiResponse<NoteResponse> createNote(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long organizationId,
            @RequestBody @Valid NoteCreateRequest request
    ) {
        NoteResponse response = noteService.createNote(
                request, 
                organizationId,
                customUserDetails.getMember().getMemberId()
        );
        
        return ApiResponse.onSuccess(SuccessStatus.NOTE_CREATED_SUCCESS, response);
    }
    
    /**
     * 조직의 모든 노트 조회
     */
    @GetMapping
    public ApiResponse<List<NoteResponse>> getNotesByOrganization(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long organizationId
    ) {
        List<NoteResponse> responses = noteService.getNotesByOrganization(
                organizationId,
                customUserDetails.getMember().getMemberId()
        );
        
        return ApiResponse.onSuccess(SuccessStatus.GET_ORGANIZATION_NOTES_SUCCESS, responses);
    }
    
    /**
     * 특정 날짜 이후의 조직 노트 조회
     */
    @GetMapping("/after")
    public ApiResponse<List<NoteResponse>> getNotesByOrganizationAfterDate(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long organizationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime
    ) {
        List<NoteResponse> responses = noteService.getNotesByOrganizationAfterDate(
                organizationId,
                dateTime,
                customUserDetails.getMember().getMemberId()
        );
        
        return ApiResponse.onSuccess(SuccessStatus.GET_ORGANIZATION_NOTES_SUCCESS, responses);
    }
    

     /* 노트 상세 조회

    @GetMapping("/{noteId}")
    public ApiResponse<NoteResponse> getNoteDetail(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long organizationId,
            @PathVariable Long noteId
    ) {
        NoteResponse response = noteService.getNoteDetail(
                noteId,
                customUserDetails.getMember().getMemberId()
        );
        
        return ApiResponse.onSuccess(SuccessStatus.GET_NOTE_DETAIL_SUCCESS, response);
    }

      */
    
    /**
     * 노트 삭제
     */
    @DeleteMapping("/{noteId}")
    public ApiResponse<Boolean> deleteNote(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long organizationId,
            @PathVariable Long noteId
    ) {
        boolean result = noteService.deleteNote(
                noteId,
                customUserDetails.getMember().getMemberId()
        );
        
        return ApiResponse.onSuccess(SuccessStatus.NOTE_DELETED_SUCCESS, result);
    }
}