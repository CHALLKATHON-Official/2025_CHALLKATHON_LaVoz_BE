package com.LaVoz.LaVoz.search.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import com.LaVoz.LaVoz.domain.Note;
import com.LaVoz.LaVoz.repository.NoteRepository;
import com.LaVoz.LaVoz.search.document.NoteDocument;
import com.LaVoz.LaVoz.search.repository.NoteSearchRepository;
import com.LaVoz.LaVoz.web.dto.response.NoteResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteSearchService {

    private final NoteRepository noteRepository;
    private final NoteSearchRepository noteSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * 애플리케이션 시작 시 모든 노트를 Elasticsearch에 인덱싱
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void indexAllNotes() {
        List<Note> allNotes = noteRepository.findAll();
        List<NoteDocument> noteDocuments = allNotes.stream()
                .map(NoteDocument::from)
                .collect(Collectors.toList());

        noteSearchRepository.saveAll(noteDocuments);
    }

    /**
     * 새 노트가 생성될 때 Elasticsearch에 인덱싱
     */
    public void indexNote(Note note) {
        NoteDocument noteDocument = NoteDocument.from(note);
        noteSearchRepository.save(noteDocument);
    }

    /**
     * 노트가 삭제될 때 Elasticsearch에서도 삭제
     */
    public void deleteNoteIndex(Long noteId) {
        noteSearchRepository.deleteById(noteId);
    }

    /**
     * 키워드를 사용한 기본 검색
     */
    public List<NoteResponse> searchNotesByKeyword(String keyword) {
        // 기본 키워드 검색(제목, 내용에서 검색)
        List<NoteDocument> noteDocuments = noteSearchRepository.findByTitleContainingOrContentContaining(
                keyword, keyword);

        // NoteDocument를 NoteResponse로 변환
        return noteDocuments.stream()
                .map(this::convertToNoteResponse)
                .collect(Collectors.toList());
    }

    /**
     * 특정 조직 내에서 키워드 검색
     */
    public List<NoteResponse> searchNotesByKeywordInOrganization(String keyword, Long organizationId) {
        List<NoteDocument> noteDocuments = noteSearchRepository
                .findByOrganizationIdAndTitleContainingOrOrganizationIdAndContentContaining(
                        organizationId, keyword, organizationId, keyword);

        return noteDocuments.stream()
                .map(this::convertToNoteResponse)
                .collect(Collectors.toList());
    }

    /**
     * 유사도 기반 고급 검색 (Fuzzy Search)
     */
    public List<NoteResponse> searchNotesBySimilarity(String keyword, Long organizationId) {
        // 쿼리 빌더 생성
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        // 조직 필터 추가 (optional)
        if (organizationId != null) {
            boolQueryBuilder.must(q -> q
                    .term(t -> t
                            .field("organizationId")
                            .value(organizationId)
                    )
            );
        }

        // 유사도 검색 쿼리 추가 (제목과 내용에서 검색)
        boolQueryBuilder.should(q -> q
                .match(m -> m
                        .field("title")
                        .query(keyword)
                        .fuzziness("AUTO")
                        .prefixLength(3)
                )
        );

        boolQueryBuilder.should(q -> q
                .match(m -> m
                        .field("content")
                        .query(keyword)
                        .fuzziness("AUTO")
                        .prefixLength(1)
                )
        );

        // 최소 매치 조건 설정
        boolQueryBuilder.minimumShouldMatch("1");

        // 검색 쿼리 생성
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(boolQueryBuilder.build()._toQuery())
                .build();

        // 검색 실행
        SearchHits<NoteDocument> searchHits = elasticsearchOperations.search(
                searchQuery, NoteDocument.class);

        // 결과 변환 및 반환
        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(this::convertToNoteResponse)
                .collect(Collectors.toList());
    }

    // NoteDocument를 NoteResponse로 변환하는 헬퍼 메서드
    private NoteResponse convertToNoteResponse(NoteDocument noteDocument) {
        // ID로 실제 Note 엔티티를 조회해 최신 데이터로 응답
        Note note = noteRepository.findById(noteDocument.getId())
                .orElseThrow(() -> new IllegalArgumentException("Note not found with id: " + noteDocument.getId()));

        return NoteResponse.from(note);
    }

}
