package com.LaVoz.LaVoz.web.controller;

import com.LaVoz.LaVoz.common.security.CustomUserDetails;
import com.LaVoz.LaVoz.service.CommentService;
import com.LaVoz.LaVoz.web.apiResponse.ApiResponse;
import com.LaVoz.LaVoz.web.apiResponse.success.SuccessStatus;
import com.LaVoz.LaVoz.web.dto.request.CommentCreateRequest;
import com.LaVoz.LaVoz.web.dto.response.CommentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notes/{noteId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 노트에 새 댓글 작성
     */
    @PostMapping
    public ApiResponse<CommentResponse> createComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long noteId,
            @RequestBody @Valid CommentCreateRequest request
    ) {
        CommentResponse response = commentService.createComment(
                request,
                noteId,
                customUserDetails.getMember().getMemberId()
        );

        return ApiResponse.onSuccess(SuccessStatus.COMMENT_CREATED_SUCCESS, response);
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/{commentId}")
    public ApiResponse<Boolean> deleteComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long noteId,
            @PathVariable Long commentId
    ) {
        boolean result = commentService.deleteComment(
                commentId,
                customUserDetails.getMember().getMemberId()
        );

        return ApiResponse.onSuccess(SuccessStatus.COMMENT_DELETED_SUCCESS, result);
    }
}