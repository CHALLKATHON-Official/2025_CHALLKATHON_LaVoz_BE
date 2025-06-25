package com.LaVoz.LaVoz.web.controller;

import com.LaVoz.LaVoz.common.security.CustomUserDetails;
import com.LaVoz.LaVoz.service.NoteService;
import com.LaVoz.LaVoz.web.dto.response.NoteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notes")
public class NoteSearchController {

    private final NoteService noteService;

    /**
     * 조직 내에서 키워드로 노트 검색
     */
    @GetMapping("/organization/{organizationId}/search")
    public ResponseEntity<List<NoteResponse>> searchNotesByOrganization(
            @PathVariable Long organizationId,
            @RequestParam String keyword,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Long memberId = customUserDetails.getMember().getMemberId();
        List<NoteResponse> notes = noteService.searchNotesByOrganization(keyword, organizationId, memberId);
        return ResponseEntity.ok(notes);
    }

    /**
     * 유사도 기반 노트 검색
     */
    @GetMapping("/search/similarity")
    public ResponseEntity<List<NoteResponse>> searchNotesBySimilarity(
            @RequestParam String keyword,
            @RequestParam(required = false) Long organizationId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Long memberId = customUserDetails.getMember().getMemberId();
        List<NoteResponse> notes = noteService.searchNotesBySimilarity(keyword, organizationId, memberId);
        return ResponseEntity.ok(notes);
    }
}
