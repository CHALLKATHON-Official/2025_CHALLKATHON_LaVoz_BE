package com.LaVoz.LaVoz.repository;

import com.LaVoz.LaVoz.domain.Comment;
import com.LaVoz.LaVoz.domain.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    /**
     * 특정 노트의 모든 댓글을 생성일 순으로 조회
     */
    List<Comment> findByNoteOrderByCreatedAtAsc(Note note);
    
    /**
     * 특정 노트의 모든 댓글 수 조회
     */
    long countByNote(Note note);
    
    /**
     * 특정 멤버가 작성한 댓글 조회
     */
    List<Comment> findByMember_MemberId(Long memberId);
    
    /**
     * 특정 노트의 댓글 삭제
     */
    void deleteByNote(Note note);
}