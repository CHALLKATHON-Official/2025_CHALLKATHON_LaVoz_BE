package com.LaVoz.LaVoz.repository;

import com.LaVoz.LaVoz.domain.Note;
import com.LaVoz.LaVoz.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    // 해당 organization의 모든 노트를 생성일 순으로 조회
    List<Note> findByOrganizationOrderByCreatedAtAsc(Organization organization);

    // 해당 organization의 특정 날짜 이후 노트들을 생성일 순으로 조회
    List<Note> findByOrganizationAndCreatedAtAfterOrderByCreatedAtAsc(Organization organization, LocalDateTime createdAt);
}
