package com.LaVoz.LaVoz.service;

import com.LaVoz.LaVoz.domain.Member;
import com.LaVoz.LaVoz.domain.Note;
import com.LaVoz.LaVoz.domain.Organization;
import com.LaVoz.LaVoz.repository.MemberOrganizationRepository;
import com.LaVoz.LaVoz.repository.MemberRepository;
import com.LaVoz.LaVoz.repository.NoteRepository;
import com.LaVoz.LaVoz.repository.OrganizationRepository;
import com.LaVoz.LaVoz.web.dto.request.NoteCreateRequest;
import com.LaVoz.LaVoz.web.dto.response.NoteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberOrganizationRepository memberOrganizationRepository;

    @InjectMocks
    private NoteService noteService;

    private Member testMember;
    private Organization testOrganization;
    private Note testNote;
    private NoteCreateRequest createRequest;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        testMember = Member.builder()
                .memberId(1L)
                .email("test@example.com")
                .name("테스트 사용자")
                .build();

        testOrganization = Organization.builder()
                .organizationId(1L)
                .name("테스트 조직")
                .build();

        testNote = Note.builder()
                .noteId(1L)
                .title("테스트 노트")
                .content("테스트 내용")
                .emotion("happy")
                .time("오후 3시")
                .member(testMember)
                .organization(testOrganization)
                .build();

        createRequest = NoteCreateRequest.builder()
                .title("새 노트")
                .content("새 노트 내용")
                .emotion("excited")
                .time("오전 10시")
                .build();

        testDateTime = LocalDateTime.now().minusDays(1);
    }

    @Test
    @DisplayName("노트 생성 성공")
    void createNote_Success() {
        // given
        Long organizationId = 1L;
        Long memberId = 1L;

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(testOrganization));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
        when(memberOrganizationRepository.existsByMember_MemberIdAndOrganization_OrganizationId(
                memberId, organizationId)).thenReturn(true);
        when(noteRepository.save(any(Note.class))).thenReturn(testNote);

        // when
        NoteResponse response = noteService.createNote(createRequest, organizationId, memberId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getNoteId()).isEqualTo(testNote.getNoteId());
        assertThat(response.getTitle()).isEqualTo(testNote.getTitle());
        assertThat(response.getContent()).isEqualTo(testNote.getContent());
        assertThat(response.getEmotion()).isEqualTo(testNote.getEmotion());
        assertThat(response.getTime()).isEqualTo(testNote.getTime());
        assertThat(response.getMemberId()).isEqualTo(testMember.getMemberId());
        assertThat(response.getOrganizationId()).isEqualTo(testOrganization.getOrganizationId());

        verify(organizationRepository, times(1)).findById(organizationId);
        verify(memberRepository, times(1)).findById(memberId);
        verify(memberOrganizationRepository, times(1))
                .existsByMember_MemberIdAndOrganization_OrganizationId(memberId, organizationId);
        verify(noteRepository, times(1)).save(any(Note.class));
    }

    @Test
    @DisplayName("존재하지 않는 조직으로 노트 생성 시 예외 발생")
    void createNote_OrganizationNotFound() {
        // given
        Long organizationId = 999L;
        Long memberId = 1L;

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            noteService.createNote(createRequest, organizationId, memberId);
        });

        assertThat(exception.getMessage()).contains("Organization not found");
        verify(organizationRepository, times(1)).findById(organizationId);
        verify(memberRepository, never()).findById(any());
        verify(memberOrganizationRepository, never())
                .existsByMember_MemberIdAndOrganization_OrganizationId(any(), any());
        verify(noteRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 멤버로 노트 생성 시 예외 발생")
    void createNote_MemberNotFound() {
        // given
        Long organizationId = 1L;
        Long memberId = 999L;

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(testOrganization));
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            noteService.createNote(createRequest, organizationId, memberId);
        });

        assertThat(exception.getMessage()).contains("Member not found");
        verify(organizationRepository, times(1)).findById(organizationId);
        verify(memberRepository, times(1)).findById(memberId);
        verify(memberOrganizationRepository, never())
                .existsByMember_MemberIdAndOrganization_OrganizationId(any(), any());
        verify(noteRepository, never()).save(any());
    }

    @Test
    @DisplayName("조직에 속하지 않은 멤버가 노트 생성 시 예외 발생")
    void createNote_MemberNotInOrganization() {
        // given
        Long organizationId = 1L;
        Long memberId = 1L;

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(testOrganization));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
        when(memberOrganizationRepository.existsByMember_MemberIdAndOrganization_OrganizationId(
                memberId, organizationId)).thenReturn(false);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            noteService.createNote(createRequest, organizationId, memberId);
        });

        assertThat(exception.getMessage()).contains("does not belong to Organization");
        verify(organizationRepository, times(1)).findById(organizationId);
        verify(memberRepository, times(1)).findById(memberId);
        verify(memberOrganizationRepository, times(1))
                .existsByMember_MemberIdAndOrganization_OrganizationId(memberId, organizationId);
        verify(noteRepository, never()).save(any());
    }

    @Test
    @DisplayName("조직의 노트 목록 조회 성공")
    void getNotesByOrganization_Success() {
        // given
        Long organizationId = 1L;
        Long memberId = 1L;
        
        Note note1 = Note.builder()
                .noteId(1L)
                .title("노트 1")
                .content("내용 1")
                .emotion("happy")
                .member(testMember)
                .organization(testOrganization)
                .build();
        
        Note note2 = Note.builder()
                .noteId(2L)
                .title("노트 2")
                .content("내용 2")
                .emotion("sad")
                .member(testMember)
                .organization(testOrganization)
                .build();
        
        List<Note> noteList = Arrays.asList(note1, note2);

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(testOrganization));
        when(memberOrganizationRepository.existsByMember_MemberIdAndOrganization_OrganizationId(
                memberId, organizationId)).thenReturn(true);
        when(noteRepository.findByOrganizationOrderByCreatedAtAsc(testOrganization)).thenReturn(noteList);

        // when
        List<NoteResponse> responses = noteService.getNotesByOrganization(organizationId, memberId);

        // then
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getNoteId()).isEqualTo(note1.getNoteId());
        assertThat(responses.get(1).getNoteId()).isEqualTo(note2.getNoteId());
        
        verify(organizationRepository, times(1)).findById(organizationId);
        verify(memberOrganizationRepository, times(1))
                .existsByMember_MemberIdAndOrganization_OrganizationId(memberId, organizationId);
        verify(noteRepository, times(1)).findByOrganizationOrderByCreatedAtAsc(testOrganization);
    }

    @Test
    @DisplayName("존재하지 않는 조직의 노트 목록 조회 시 예외 발생")
    void getNotesByOrganization_OrganizationNotFound() {
        // given
        Long organizationId = 999L;
        Long memberId = 1L;

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            noteService.getNotesByOrganization(organizationId, memberId);
        });

        assertThat(exception.getMessage()).contains("Organization not found");
        verify(organizationRepository, times(1)).findById(organizationId);
        verify(memberOrganizationRepository, never())
                .existsByMember_MemberIdAndOrganization_OrganizationId(any(), any());
        verify(noteRepository, never()).findByOrganizationOrderByCreatedAtAsc(any());
    }

    @Test
    @DisplayName("조직에 속하지 않은 멤버가 노트 목록 조회 시 예외 발생")
    void getNotesByOrganization_MemberNotInOrganization() {
        // given
        Long organizationId = 1L;
        Long memberId = 1L;

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(testOrganization));
        when(memberOrganizationRepository.existsByMember_MemberIdAndOrganization_OrganizationId(
                memberId, organizationId)).thenReturn(false);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            noteService.getNotesByOrganization(organizationId, memberId);
        });

        assertThat(exception.getMessage()).contains("does not belong to Organization");
        verify(organizationRepository, times(1)).findById(organizationId);
        verify(memberOrganizationRepository, times(1))
                .existsByMember_MemberIdAndOrganization_OrganizationId(memberId, organizationId);
        verify(noteRepository, never()).findByOrganizationOrderByCreatedAtAsc(any());
    }

    @Test
    @DisplayName("특정 날짜 이후의 조직 노트 목록 조회 성공")
    void getNotesByOrganizationAfterDate_Success() {
        // given
        Long organizationId = 1L;
        Long memberId = 1L;
        
        Note note1 = Note.builder()
                .noteId(1L)
                .title("노트 1")
                .content("내용 1")
                .emotion("happy")
                .member(testMember)
                .organization(testOrganization)
                .build();
        
        List<Note> noteList = List.of(note1);

        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(testOrganization));
        when(memberOrganizationRepository.existsByMember_MemberIdAndOrganization_OrganizationId(
                memberId, organizationId)).thenReturn(true);
        when(noteRepository.findByOrganizationAndCreatedAtAfterOrderByCreatedAtAsc(testOrganization, testDateTime))
                .thenReturn(noteList);

        // when
        List<NoteResponse> responses = noteService.getNotesByOrganizationAfterDate(organizationId, testDateTime, memberId);

        // then
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getNoteId()).isEqualTo(note1.getNoteId());
        
        verify(organizationRepository, times(1)).findById(organizationId);
        verify(memberOrganizationRepository, times(1))
                .existsByMember_MemberIdAndOrganization_OrganizationId(memberId, organizationId);
        verify(noteRepository, times(1))
                .findByOrganizationAndCreatedAtAfterOrderByCreatedAtAsc(testOrganization, testDateTime);
    }

    @Test
    @DisplayName("노트 삭제 성공 (노트 작성자)")
    void deleteNote_SuccessAsMember() {
        // given
        Long noteId = 1L;
        Long memberId = 1L; // 노트 작성자와 동일

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(testNote));
        doNothing().when(noteRepository).delete(testNote);

        // when
        boolean result = noteService.deleteNote(noteId, memberId);

        // then
        assertThat(result).isTrue();
        verify(noteRepository, times(1)).findById(noteId);
        verify(noteRepository, times(1)).delete(testNote);
        verify(memberOrganizationRepository, never())
                .existsByMember_MemberIdAndOrganization_OrganizationId(any(), any());
    }

    @Test
    @DisplayName("노트 삭제 성공 (동일 조직 멤버)")
    void deleteNote_SuccessAsOrganizationMember() {
        // given
        Long noteId = 1L;
        Long memberId = 2L; // 노트 작성자와 다름
        
        Note note = Note.builder()
                .noteId(noteId)
                .title("테스트 노트")
                .content("테스트 내용")
                .emotion("happy")
                .member(Member.builder().memberId(1L).build()) // 다른 작성자
                .organization(testOrganization)
                .build();

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));
        when(memberOrganizationRepository.existsByMember_MemberIdAndOrganization_OrganizationId(
                memberId, note.getOrganization().getOrganizationId())).thenReturn(true);
        doNothing().when(noteRepository).delete(note);

        // when
        boolean result = noteService.deleteNote(noteId, memberId);

        // then
        assertThat(result).isTrue();
        verify(noteRepository, times(1)).findById(noteId);
        verify(memberOrganizationRepository, times(1))
                .existsByMember_MemberIdAndOrganization_OrganizationId(
                        memberId, note.getOrganization().getOrganizationId());
        verify(noteRepository, times(1)).delete(note);
    }

    @Test
    @DisplayName("존재하지 않는 노트 삭제 시 예외 발생")
    void deleteNote_NoteNotFound() {
        // given
        Long noteId = 999L;
        Long memberId = 1L;

        when(noteRepository.findById(noteId)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            noteService.deleteNote(noteId, memberId);
        });

        assertThat(exception.getMessage()).contains("Note not found");
        verify(noteRepository, times(1)).findById(noteId);
        verify(noteRepository, never()).delete(any());
    }

    @Test
    @DisplayName("권한 없는 멤버가 노트 삭제 시 예외 발생")
    void deleteNote_Unauthorized() {
        // given
        Long noteId = 1L;
        Long memberId = 3L; // 노트 작성자와 다름
        
        Note note = Note.builder()
                .noteId(noteId)
                .title("테스트 노트")
                .content("테스트 내용")
                .emotion("happy")
                .member(Member.builder().memberId(1L).build()) // 다른 작성자
                .organization(testOrganization)
                .build();

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));
        when(memberOrganizationRepository.existsByMember_MemberIdAndOrganization_OrganizationId(
                memberId, note.getOrganization().getOrganizationId())).thenReturn(false);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            noteService.deleteNote(noteId, memberId);
        });

        assertThat(exception.getMessage()).contains("does not have permission to delete Note");
        verify(noteRepository, times(1)).findById(noteId);
        verify(memberOrganizationRepository, times(1))
                .existsByMember_MemberIdAndOrganization_OrganizationId(
                        memberId, note.getOrganization().getOrganizationId());
        verify(noteRepository, never()).delete(any());
    }
}