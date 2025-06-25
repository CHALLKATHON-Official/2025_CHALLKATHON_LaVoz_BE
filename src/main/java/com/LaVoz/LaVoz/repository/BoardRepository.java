package com.LaVoz.LaVoz.repository;

import com.LaVoz.LaVoz.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 최신순으로 모든 게시물 조회 (생성일시 기준 내림차순)
    List<Board> findAllByOrderByCreatedAtDesc();

    // 조회수 높은 순으로 상위 N개 게시글 조회
    List<Board> findTop10ByOrderByViewCountDesc();

}
