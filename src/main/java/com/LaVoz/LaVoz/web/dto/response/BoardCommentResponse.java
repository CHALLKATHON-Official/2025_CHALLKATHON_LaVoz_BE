package com.LaVoz.LaVoz.web.dto.response;

import com.LaVoz.LaVoz.domain.BoardComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardCommentResponse {
    private Long boardCommentId;
    private String content;
    private Long memberId;
    private String memberName;
    private String memberRole;
    private Long boardId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BoardCommentResponse from(BoardComment boardComment) {
        return BoardCommentResponse.builder()
                .boardCommentId(boardComment.getCommentId())
                .content(boardComment.getContent())
                .memberId(boardComment.getMember().getMemberId())
                .memberName(boardComment.getMember().getName())
                .memberRole(boardComment.getMember().getRole().name())
                .createdAt(boardComment.getCreatedAt())
                .updatedAt(boardComment.getUpdatedAt())
                .boardId(boardComment.getBoard().getBoardId())
                .build();
    }
}
