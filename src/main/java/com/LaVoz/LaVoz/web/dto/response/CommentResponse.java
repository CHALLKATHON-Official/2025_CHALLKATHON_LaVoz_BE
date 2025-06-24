package com.LaVoz.LaVoz.web.dto.response;

import com.LaVoz.LaVoz.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Long commentId;
    private String content;
    private Long memberId;
    private String memberName;
    private String memberRole;
    private Long noteId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .memberId(comment.getMember().getMemberId())
                .memberName(comment.getMember().getName())
                .memberRole(comment.getMember().getRole().name())
                .noteId(comment.getNote().getNoteId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}

