package com.LaVoz.LaVoz.web.controller;

import com.LaVoz.LaVoz.common.security.CustomUserDetails;
import com.LaVoz.LaVoz.domain.Board;
import com.LaVoz.LaVoz.service.BoardService;
import com.LaVoz.LaVoz.web.apiResponse.ApiResponse;
import com.LaVoz.LaVoz.web.apiResponse.success.SuccessStatus;
import com.LaVoz.LaVoz.web.dto.request.BoardCommentCreateRequest;
import com.LaVoz.LaVoz.web.dto.request.BoardCreateRequest;
import com.LaVoz.LaVoz.web.dto.response.BoardCommentResponse;
import com.LaVoz.LaVoz.web.dto.response.BoardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시글 생성
     */
    @PostMapping
    public ApiResponse<BoardResponse> createBoard(
            @Valid @RequestBody BoardCreateRequest boardCreateRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        BoardResponse response = boardService.createBoard(boardCreateRequest, customUserDetails.getMember());
        return ApiResponse.onSuccess(SuccessStatus.BOARD_CREATED_SUCCESS, response);
    }

    /**
     * 게시글 목록 조회 (최신순)
     */
    @GetMapping
    public ApiResponse<List<BoardResponse>> getBoards() {
        List<BoardResponse> boardResponses = boardService.getAllBoards();
        return ApiResponse.onSuccess(SuccessStatus.GET_BOARD_LIST_SUCCESS, boardResponses);
    }

    /**
     * 게시글 상세 조회
     */
    @GetMapping("/{boardId}")
    public ApiResponse<BoardResponse> getBoard(
            @PathVariable Long boardId,
            @AuthenticationPrincipal() CustomUserDetails customUserDetails
    ) {
        Long memberId = customUserDetails != null ?
                customUserDetails.getMember().getMemberId() : null;

        BoardResponse response = boardService.getBoardById(boardId, memberId);
        return ApiResponse.onSuccess(SuccessStatus.GET_BOARD_DETAIL_SUCCESS, response);
    }

    /**
     * 게시글 북마크 토글
     */
    @PostMapping("/{boardId}/bookmark")
    public ApiResponse<Boolean> toggleBookmark(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        boolean isBookmarked = boardService.toggleBookmark(boardId, customUserDetails.getMember());
        return ApiResponse.onSuccess(SuccessStatus.BOOKMARK_TOGGLED_SUCCESS, isBookmarked);
    }

    /**
     * 사용자의 북마크한 게시글 목록 조회
     */
    @GetMapping("/bookmarks")
    public ApiResponse<List<BoardResponse>> getBookmarkedBoards(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        List<BoardResponse> bookmarkedBoards =
                boardService.getBookmarkedBoards(customUserDetails.getMember().getMemberId());
        return ApiResponse.onSuccess(SuccessStatus.GET_BOOKMARKED_BOARDS_SUCCESS, bookmarkedBoards);
    }


    /**
     * 게시글 수정
     */
    @PutMapping("/{boardId}")
    public ApiResponse<BoardResponse> updateBoard(
            @PathVariable Long boardId,
            @Valid @RequestBody BoardCreateRequest boardCreateRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        BoardResponse response = boardService.updateBoard(boardId, boardCreateRequest,customUserDetails.getMember().getMemberId());
        return ApiResponse.onSuccess(SuccessStatus.BOARD_UPDATED_SUCCESS, response);
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{boardId}")
    public ApiResponse<Void> deleteBoard(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        boardService.deleteBoard(boardId, customUserDetails.getMember().getMemberId());
        return ApiResponse.onSuccess(SuccessStatus.BOARD_DELETED_SUCCESS, null);
    }

    /**
     * 댓글 생성
     */
    @PostMapping("/{boardId}/comments")
    public ApiResponse<BoardCommentResponse> createComment(
            @PathVariable Long boardId,
            @Valid @RequestBody BoardCommentCreateRequest commentCreateRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        BoardCommentResponse response = boardService.createComment(boardId, commentCreateRequest, customUserDetails.getMember());
        return ApiResponse.onSuccess(SuccessStatus.COMMENT_CREATED_SUCCESS, response);
    }

    /**
     * 특정 게시글의 댓글 목록 조회
     */
    @GetMapping("/{boardId}/comments")
    public ApiResponse<List<BoardCommentResponse>> getCommentsByBoardId(@PathVariable Long boardId) {
        List<BoardCommentResponse> comments = boardService.getCommentsByBoardId(boardId);
        return ApiResponse.onSuccess(SuccessStatus.GET_BOARD_COMMENTS_SUCCESS, comments);
    }

    /**
     * 댓글 수정
     */
    @PutMapping("/comments/{commentId}")
    public ApiResponse<BoardCommentResponse> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody BoardCommentCreateRequest commentCreateRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        BoardCommentResponse response = boardService.updateComment(commentId, commentCreateRequest, customUserDetails.getMember().getMemberId());
        return ApiResponse.onSuccess(SuccessStatus.COMMENT_UPDATED_SUCCESS, response);
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        boardService.deleteComment(commentId, customUserDetails.getMember().getMemberId());
        return ApiResponse.onSuccess(SuccessStatus.COMMENT_DELETED_SUCCESS, null);
    }

    /**
     * 조회수 높은 게시글 상위 10개 조회
     */
    @GetMapping("/top-viewed")
    public ApiResponse<List<BoardResponse>> getTopViewedBoards() {
        List<BoardResponse> topBoards = boardService.getTopViewedBoards();
        return ApiResponse.onSuccess(SuccessStatus.GET_TOP_VIEWED_BOARDS_SUCCESS, topBoards);
    }

}
