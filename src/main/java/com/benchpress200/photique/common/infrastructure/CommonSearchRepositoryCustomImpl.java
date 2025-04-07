package com.benchpress200.photique.common.infrastructure;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;

@RequiredArgsConstructor
public class CommonSearchRepositoryCustomImpl implements CommonSearchRepositoryCustom {
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public void update(final Object document) {
        Document esDocument = elasticsearchOperations.getElasticsearchConverter().mapObject(document);
        UpdateQuery updateQuery = UpdateQuery.builder(esDocument.getId())
                .withDocument(esDocument)
                .withDocAsUpsert(true)
                .build();

        String indexName = Objects.requireNonNull(elasticsearchOperations.getElasticsearchConverter()
                        .getMappingContext()
                        .getPersistentEntity(document.getClass())) // 엔티티 정보를 가져옴
                .getIndexCoordinates() // 인덱스 정보 추출
                .getIndexName(); // 인덱스명 가져오기

        elasticsearchOperations.update(updateQuery, IndexCoordinates.of(indexName));
    }

    @Override
    public void updateAll(final List<?> documents) {
        if (documents == null || documents.isEmpty()) {
            return; // 빈 리스트일 경우 아무 작업도 하지 않음
        }

        List<UpdateQuery> updateQueries = documents.stream().map(document -> {
            Document esDocument = elasticsearchOperations.getElasticsearchConverter().mapObject(document);
            // 없으면 삽입, 있으면 업데이트
            return UpdateQuery.builder(esDocument.getId())
                    .withDocument(esDocument)
                    .withDocAsUpsert(true) // 없으면 삽입, 있으면 업데이트
                    .build();
        }).toList();

        // 공통 인덱스명 가져오기 (첫 번째 문서 기준)
        Object firstDocument = documents.get(0);
        String indexName = Objects.requireNonNull(elasticsearchOperations.getElasticsearchConverter()
                        .getMappingContext()
                        .getPersistentEntity(firstDocument.getClass()))
                .getIndexCoordinates()
                .getIndexName();

        // Bulk 업데이트 실행
        elasticsearchOperations.bulkUpdate(updateQueries, IndexCoordinates.of(indexName));
    }

}
