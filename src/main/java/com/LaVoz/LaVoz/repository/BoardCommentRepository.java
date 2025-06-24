package com.LaVoz.LaVoz.repository;

import com.LaVoz.LaVoz.domain.BoardComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {
    // 특정 게시물의 댓글 조회 (생성일시 기준 오름차순 - 처음 만들어진 순)
    List<BoardComment> findByBoardBoardIdOrderByCreatedAtAsc(Long boardId);
}
