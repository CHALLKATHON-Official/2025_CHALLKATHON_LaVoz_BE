package com.LaVoz.LaVoz.search.repository;

import com.LaVoz.LaVoz.search.document.NoteDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteSearchRepository extends ElasticsearchRepository<NoteDocument, Long> {

    // 제목이나 내용에 검색어를 포함하는 노트 검색
    List<NoteDocument> findByTitleContainingOrContentContaining(String titleKeyword, String contentKeyword);

    // 특정 조직의 노트 중 검색어를 포함하는 노트 검색
    List<NoteDocument> findByOrganizationIdAndTitleContainingOrOrganizationIdAndContentContaining(
            Long orgId1, String titleKeyword, Long orgId2, String contentKeyword);
}

