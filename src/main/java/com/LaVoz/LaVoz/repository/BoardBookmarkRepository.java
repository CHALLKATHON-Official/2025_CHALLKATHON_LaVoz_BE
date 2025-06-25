package com.LaVoz.LaVoz.repository;

import com.LaVoz.LaVoz.domain.BoardBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardBookmarkRepository extends JpaRepository<BoardBookmark, Long> {
    Optional<BoardBookmark> findByMemberMemberIdAndBoardBoardId(Long memberId, Long boardId);
    List<BoardBookmark> findByMemberMemberIdAndIsBookmarkedTrue(Long memberId);
}
