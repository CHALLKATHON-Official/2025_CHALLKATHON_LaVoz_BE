package com.LaVoz.LaVoz.web.dto.response;

import com.LaVoz.LaVoz.domain.Note;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteResponse {
    private Long noteId;
    private String title;
    private String content;
    private String emotion;
    private String time;
    private Long memberId;
    private String memberName;
    private String memberRole;
    private Long organizationId;
    private String organizationName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentResponse> comments;

    public static NoteResponse from(Note note) {
        return NoteResponse.builder()
                .noteId(note.getNoteId())
                .title(note.getTitle())
                .content(note.getContent())
                .emotion(note.getEmotion())
                .time(note.getTime())
                .memberId(note.getMember().getMemberId())
                .memberName(note.getMember().getName())
                .memberRole(note.getMember().getRole().name())
                .organizationId(note.getOrganization() != null ? note.getOrganization().getOrganizationId() : null)
                .organizationName(note.getOrganization() != null ? note.getOrganization().getName() : null)
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .comments(Optional.ofNullable(note.getComments())
                          .map(comments -> comments.stream().map(CommentResponse::from).toList())
                          .orElse(List.of()))
                .build();
    }
}