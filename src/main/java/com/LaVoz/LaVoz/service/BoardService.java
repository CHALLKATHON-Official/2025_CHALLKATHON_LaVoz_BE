package com.LaVoz.LaVoz.service;

import com.LaVoz.LaVoz.domain.Board;
import com.LaVoz.LaVoz.domain.BoardBookmark;
import com.LaVoz.LaVoz.domain.BoardComment;
import com.LaVoz.LaVoz.domain.Member;
import com.LaVoz.LaVoz.repository.BoardBookmarkRepository;
import com.LaVoz.LaVoz.repository.BoardCommentRepository;
import com.LaVoz.LaVoz.repository.BoardRepository;
import com.LaVoz.LaVoz.web.dto.request.BoardCommentCreateRequest;
import com.LaVoz.LaVoz.web.dto.request.BoardCreateRequest;
import com.LaVoz.LaVoz.web.dto.response.BoardCommentResponse;
import com.LaVoz.LaVoz.web.dto.response.BoardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final BoardBookmarkRepository boardBookmarkRepository;

    @Transactional
    public BoardResponse createBoard(BoardCreateRequest boardCreateRequest, Member member) {
        // 게시글 생성
        Board board = Board.builder()
                .title(boardCreateRequest.getTitle())
                .content(boardCreateRequest.getContent())
                .member(member)
                .build();

        Board savedBoard = boardRepository.save(board);
        return BoardResponse.from(savedBoard);
    }

    /**
     * 게시글 목록 조회 (최신순)
     */
    public List<BoardResponse> getAllBoards() {
        List<Board> boards = boardRepository.findAllByOrderByCreatedAtDesc();
        return boards.stream()
                .map(BoardResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 게시물 상세 조회
     */
    @Transactional
    public BoardResponse getBoardById(Long id, Long memberId) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        // 조회수 증가
        board.increaseViewCount();
        // 북마크 여부 확인
        boolean isBookmarked = false;
        if (memberId != null) {
            isBookmarked = boardBookmarkRepository
                    .findByMemberMemberIdAndBoardBoardId(memberId, id)
                    .map(BoardBookmark::isBookmarked)
                    .orElse(false);
        }


        return BoardResponse.from(board, isBookmarked);
    }

    /**
     * 북마크 토글
     */
    @Transactional
    public boolean toggleBookmark(Long boardId, Member member) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        Optional<BoardBookmark> bookmarkOptional = boardBookmarkRepository
                .findByMemberMemberIdAndBoardBoardId(member.getMemberId(), boardId);

        if (bookmarkOptional.isPresent()) {
            BoardBookmark bookmark = bookmarkOptional.get();
            bookmark.toggleBookmark();
            return bookmark.isBookmarked();
        } else {
            BoardBookmark bookmark = BoardBookmark.builder()
                    .member(member)
                    .board(board)
                    .build();
            boardBookmarkRepository.save(bookmark);
            return true;
        }
    }

    /**
     * 사용자의 북마크한 게시글 목록 조회
     */
    public List<BoardResponse> getBookmarkedBoards(Long memberId) {
        List<BoardBookmark> bookmarks = boardBookmarkRepository
                .findByMemberMemberIdAndIsBookmarkedTrue(memberId);

        return bookmarks.stream()
                .map(bookmark -> BoardResponse.from(bookmark.getBoard(), true))
                .collect(Collectors.toList());
    }

    /**
     * 조회수 높은 순으로 상위 10개 게시글 조회
     */
    public List<BoardResponse> getTopViewedBoards() {
        List<Board> topBoards = boardRepository.findTop10ByOrderByViewCountDesc();
        return topBoards.stream()
                .map(BoardResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public BoardResponse updateBoard(Long boardId, BoardCreateRequest boardCreateRequest, Long memberId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 작성자 확인
        if (!board.getMember().getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("게시글 수정 권한이 없습니다.");
        }

        board.updateBoard(boardCreateRequest.getTitle(), boardCreateRequest.getContent());
        return BoardResponse.from(board);
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deleteBoard(Long boardId, Long memberId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 작성자 확인
        if (!board.getMember().getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("게시글 삭제 권한이 없습니다.");
        }

        boardRepository.delete(board);
    }

    /**
     * 댓글 생성
     */
    @Transactional
    public BoardCommentResponse createComment(Long boardId, BoardCommentCreateRequest commentCreateRequest, Member member) {
        // 게시글 조회
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 댓글 생성
        BoardComment boardComment = BoardComment.builder()
                .content(commentCreateRequest.getContent())
                .member(member)
                .board(board)
                .build();

        BoardComment savedComment = boardCommentRepository.save(boardComment);
        return BoardCommentResponse.from(savedComment);
    }

    /**
     * 특정 게시글의 댓글 목록 조회 (생성일시 기준 오름차순)
     */
    public List<BoardCommentResponse> getCommentsByBoardId(Long boardId) {
        // 게시글 존재 확인
        boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        List<BoardComment> comments = boardCommentRepository.findByBoardBoardIdOrderByCreatedAtAsc(boardId);
        return comments.stream()
                .map(BoardCommentResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public BoardCommentResponse updateComment(Long commentId, BoardCommentCreateRequest commentCreateRequest, Long memberId) {
        BoardComment comment = boardCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        // 작성자 확인
        if (!comment.getMember().getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
        }

        comment.updateComment(commentCreateRequest.getContent());
        return BoardCommentResponse.from(comment);
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        BoardComment comment = boardCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        // 작성자 확인
        if (!comment.getMember().getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
        }

        boardCommentRepository.delete(comment);
    }
}
