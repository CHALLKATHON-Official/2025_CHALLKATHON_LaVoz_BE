package com.LaVoz.LaVoz.web.dto.response;

import com.LaVoz.LaVoz.domain.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardResponse {

    private Long boardId;
    private String title;
    private String content;
    private Long memberId;
    private String memberName;
    private int viewCount;
    private boolean isBookmarked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BoardResponse from(Board board) {
        return BoardResponse.builder()
                .boardId(board.getBoardId())
                .title(board.getTitle())
                .content(board.getContent())
                .memberId(board.getMember().getMemberId())
                .memberName(board.getMember().getName())
                .viewCount(board.getViewCount())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }

    public static BoardResponse from(Board board, boolean isBookmarked) {
        return BoardResponse.builder()
                .boardId(board.getBoardId())
                .title(board.getTitle())
                .content(board.getContent())
                .memberId(board.getMember().getMemberId())
                .memberName(board.getMember().getName())
                .viewCount(board.getViewCount())
                .isBookmarked(isBookmarked)
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();

    }
}
