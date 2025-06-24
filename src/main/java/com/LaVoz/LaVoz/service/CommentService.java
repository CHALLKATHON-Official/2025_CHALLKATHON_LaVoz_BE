package com.LaVoz.LaVoz.service;

import com.LaVoz.LaVoz.domain.Comment;
import com.LaVoz.LaVoz.domain.Member;
import com.LaVoz.LaVoz.domain.Note;
import com.LaVoz.LaVoz.domain.Organization;
import com.LaVoz.LaVoz.repository.CommentRepository;
import com.LaVoz.LaVoz.repository.MemberOrganizationRepository;
import com.LaVoz.LaVoz.repository.MemberRepository;
import com.LaVoz.LaVoz.repository.NoteRepository;
import com.LaVoz.LaVoz.web.dto.request.CommentCreateRequest;
import com.LaVoz.LaVoz.web.dto.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final NoteRepository noteRepository;
    private final MemberRepository memberRepository;
    private final MemberOrganizationRepository memberOrganizationRepository;

    /**
     * 노트에 새 댓글 작성
     */
    @Transactional
    public CommentResponse createComment(CommentCreateRequest request, Long noteId, Long memberId) {
        // 노트 조회
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("Note not found with id: " + noteId));

        // 멤버 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        // 노트가 조직에 속한 경우, 멤버가 해당 조직에 속하는지 확인
        if (note.getOrganization() != null) {
            Organization organization = note.getOrganization();
            boolean isMemberInOrganization = memberOrganizationRepository
                    .existsByMember_MemberIdAndOrganization_OrganizationId(
                            memberId, organization.getOrganizationId());

            if (!isMemberInOrganization) {
                throw new IllegalArgumentException("Member with id " + memberId +
                        " does not belong to Organization with id " + organization.getOrganizationId() +
                        " and cannot comment on this note");
            }
        }

        // 댓글 생성 및 저장
        Comment comment = Comment.builder()
                .content(request.getContent())
                .member(member)
                .note(note)
                .build();

        Comment savedComment = commentRepository.save(comment);

        return CommentResponse.from(savedComment);
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public boolean deleteComment(Long commentId, Long memberId) {
        // 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + commentId));

        // 댓글 작성자만 삭제 가능
        if (!comment.getMember().getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("Member with id " + memberId +
                    " does not have permission to delete Comment with id " + commentId);
        }

        commentRepository.delete(comment);
        return true;
    }
}