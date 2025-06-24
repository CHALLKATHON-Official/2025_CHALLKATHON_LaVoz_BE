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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoteService {
    
    private final NoteRepository noteRepository;
    private final OrganizationRepository organizationRepository;
    private final MemberRepository memberRepository;
    private final MemberOrganizationRepository memberOrganizationRepository;
    
    /**
     * 조직에 새로운 노트 생성
     */
    @Transactional
    public NoteResponse createNote(NoteCreateRequest request, Long organizationId, Long memberId) {
        // 조직 조회
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found with id: " + organizationId));
        
        // 멤버 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));
        
        // 멤버가 해당 조직에 속하는지 확인
        boolean isMemberInOrganization = memberOrganizationRepository
                .existsByMember_MemberIdAndOrganization_OrganizationId(memberId, organizationId);
        
        if (!isMemberInOrganization) {
            throw new IllegalArgumentException("Member with id " + memberId + 
                    " does not belong to Organization with id " + organizationId);
        }
        
        // 노트 생성 및 저장
        Note note = Note.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .emotion(request.getEmotion())
                .time(request.getTime())
                .member(member)
                .organization(organization)
                .build();
        
        Note savedNote = noteRepository.save(note);
        
        return NoteResponse.from(savedNote);
    }
    
    /**
     * 조직의 모든 노트 조회
     */
    public List<NoteResponse> getNotesByOrganization(Long organizationId, Long memberId) {
        // 조직 조회
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found with id: " + organizationId));
        
        // 멤버가 해당 조직에 속하는지 확인
        boolean isMemberInOrganization = memberOrganizationRepository
                .existsByMember_MemberIdAndOrganization_OrganizationId(memberId, organizationId);
        
        if (!isMemberInOrganization) {
            throw new IllegalArgumentException("Member with id " + memberId + 
                    " does not belong to Organization with id " + organizationId);
        }
        
        // 조직의 노트 조회
        List<Note> notes = noteRepository.findByOrganizationOrderByCreatedAtAsc(organization);
        
        return notes.stream()
                .map(NoteResponse::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 특정 날짜 이후의 조직 노트 조회
     */
    public List<NoteResponse> getNotesByOrganizationAfterDate(Long organizationId, LocalDateTime dateTime, Long memberId) {
        // 조직 조회
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found with id: " + organizationId));
        
        // 멤버가 해당 조직에 속하는지 확인
        boolean isMemberInOrganization = memberOrganizationRepository
                .existsByMember_MemberIdAndOrganization_OrganizationId(memberId, organizationId);
        
        if (!isMemberInOrganization) {
            throw new IllegalArgumentException("Member with id " + memberId + 
                    " does not belong to Organization with id " + organizationId);
        }
        
        // 조직의 특정 날짜 이후 노트 조회
        List<Note> notes = noteRepository.findByOrganizationAndCreatedAtAfterOrderByCreatedAtAsc(organization, dateTime);
        
        return notes.stream()
                .map(NoteResponse::from)
                .collect(Collectors.toList());
    }

    /* 노트 상세 조회 (필요 없을 듯)

    public NoteResponse getNoteDetail(Long noteId, Long memberId) {
        // 노트 조회
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("Note not found with id: " + noteId));
        
        // 멤버가 해당 노트의 조직에 속하는지 확인
        if (note.getOrganization() != null) {
            boolean isMemberInOrganization = memberOrganizationRepository
                    .existsByMember_MemberIdAndOrganization_OrganizationId(
                            memberId, note.getOrganization().getOrganizationId());
            
            if (!isMemberInOrganization) {
                throw new IllegalArgumentException("Member with id " + memberId + 
                        " does not have access to Note with id " + noteId);
            }
        } else if (!note.getMember().getMemberId().equals(memberId)) {
            // 조직이 없는 개인 노트인 경우, 작성자만 접근 가능
            throw new IllegalArgumentException("Member with id " + memberId + 
                    " does not have access to Note with id " + noteId);
        }
        
        return NoteResponse.from(note);
    }

     */

    /**
     * 노트 삭제
     */
    @Transactional
    public boolean deleteNote(Long noteId, Long memberId) {
        // 노트 조회
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("Note not found with id: " + noteId));
        
        // 노트 작성자 또는 조직 소속 멤버만 삭제 가능
        if (!note.getMember().getMemberId().equals(memberId)) {
            if (note.getOrganization() != null) {
                boolean isMemberInOrganization = memberOrganizationRepository
                        .existsByMember_MemberIdAndOrganization_OrganizationId(
                                memberId, note.getOrganization().getOrganizationId());
                
                if (!isMemberInOrganization) {
                    throw new IllegalArgumentException("Member with id " + memberId + 
                            " does not have permission to delete Note with id " + noteId);
                }
            } else {
                throw new IllegalArgumentException("Member with id " + memberId + 
                        " does not have permission to delete Note with id " + noteId);
            }
        }
        
        noteRepository.delete(note);
        return true;
    }
}