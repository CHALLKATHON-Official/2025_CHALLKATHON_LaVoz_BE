package com.LaVoz.LaVoz.service;

import com.LaVoz.LaVoz.domain.Comment;
import com.LaVoz.LaVoz.domain.Member;
import com.LaVoz.LaVoz.domain.Note;
import com.LaVoz.LaVoz.domain.Organization;
import com.LaVoz.LaVoz.domain.enums.Role;
import com.LaVoz.LaVoz.repository.CommentRepository;
import com.LaVoz.LaVoz.repository.MemberOrganizationRepository;
import com.LaVoz.LaVoz.repository.MemberRepository;
import com.LaVoz.LaVoz.repository.NoteRepository;
import com.LaVoz.LaVoz.web.dto.request.CommentCreateRequest;
import com.LaVoz.LaVoz.web.dto.response.CommentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberOrganizationRepository memberOrganizationRepository;

    @InjectMocks
    private CommentService commentService;

    private Member member;
    private Note note;
    private Organization organization;
    private Comment comment;
    private CommentCreateRequest createRequest;
    private final Long MEMBER_ID = 1L;
    private final Long NOTE_ID = 1L;
    private final Long COMMENT_ID = 1L;
    private final Long ORGANIZATION_ID = 1L;
    private final String COMMENT_CONTENT = "테스트 댓글 내용";

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .memberId(MEMBER_ID)
                .name("테스트 회원")
                .role(Role.ROLE_GUARDIAN)
                .build();

        organization = Organization.builder()
                .organizationId(ORGANIZATION_ID)
                .name("테스트 조직")
                .build();

        note = Note.builder()
                .noteId(NOTE_ID)
                .title("테스트 노트")
                .content("테스트 내용")
                .member(member)
                .organization(organization)
                .build();

        comment = Comment.builder()
                .commentId(COMMENT_ID)
                .content(COMMENT_CONTENT)
                .member(member)
                .note(note)
                .build();

        createRequest = new CommentCreateRequest(COMMENT_CONTENT);
    }

    @Test
    @DisplayName("댓글 생성 성공 - 조직에 속한 노트")
    void createComment_withOrganizationNote_success() {
        // given
        when(noteRepository.findById(NOTE_ID)).thenReturn(Optional.of(note));
        when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(member));
        when(memberOrganizationRepository.existsByMember_MemberIdAndOrganization_OrganizationId(MEMBER_ID, ORGANIZATION_ID))
                .thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // when
        CommentResponse response = commentService.createComment(createRequest, NOTE_ID, MEMBER_ID);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo(COMMENT_CONTENT);
        verify(noteRepository).findById(NOTE_ID);
        verify(memberRepository).findById(MEMBER_ID);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 생성 실패 - 노트가 존재하지 않음")
    void createComment_noteNotFound_throwsException() {
        // given
        when(noteRepository.findById(NOTE_ID)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> commentService.createComment(createRequest, NOTE_ID, MEMBER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Note not found");
    }

    @Test
    @DisplayName("댓글 생성 실패 - 회원이 존재하지 않음")
    void createComment_memberNotFound_throwsException() {
        // given
        when(noteRepository.findById(NOTE_ID)).thenReturn(Optional.of(note));
        when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> commentService.createComment(createRequest, NOTE_ID, MEMBER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Member not found");
    }

    @Test
    @DisplayName("댓글 생성 실패 - 조직에 속하지 않은 회원")
    void createComment_memberNotInOrganization_throwsException() {
        // given
        when(noteRepository.findById(NOTE_ID)).thenReturn(Optional.of(note));
        when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(member));
        when(memberOrganizationRepository.existsByMember_MemberIdAndOrganization_OrganizationId(MEMBER_ID, ORGANIZATION_ID))
                .thenReturn(false);

        // when, then
        assertThatThrownBy(() -> commentService.createComment(createRequest, NOTE_ID, MEMBER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not belong to Organization");
    }

    @Test
    @DisplayName("댓글 생성 성공 - 조직에 속하지 않은 노트")
    void createComment_withNonOrganizationNote_success() {
        // given
        Note nonOrgNote = Note.builder()
                .noteId(NOTE_ID)
                .title("테스트 노트")
                .content("테스트 내용")
                .member(member)
                .organization(null) // 조직이 없는 노트
                .build();
        
        when(noteRepository.findById(NOTE_ID)).thenReturn(Optional.of(nonOrgNote));
        when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(member));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // when
        CommentResponse response = commentService.createComment(createRequest, NOTE_ID, MEMBER_ID);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo(COMMENT_CONTENT);
        verify(noteRepository).findById(NOTE_ID);
        verify(memberRepository).findById(MEMBER_ID);
        verify(memberOrganizationRepository, never())
                .existsByMember_MemberIdAndOrganization_OrganizationId(anyLong(), anyLong());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteComment_success() {
        // given
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        // when
        boolean result = commentService.deleteComment(COMMENT_ID, MEMBER_ID);

        // then
        assertThat(result).isTrue();
        verify(commentRepository).findById(COMMENT_ID);
        verify(commentRepository).delete(comment);
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 댓글이 존재하지 않음")
    void deleteComment_commentNotFound_throwsException() {
        // given
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> commentService.deleteComment(COMMENT_ID, MEMBER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Comment not found");
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 댓글 작성자가 아님")
    void deleteComment_notCommentOwner_throwsException() {
        // given
        Long otherMemberId = 2L;
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        // when, then
        assertThatThrownBy(() -> commentService.deleteComment(COMMENT_ID, otherMemberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not have permission");
    }
}