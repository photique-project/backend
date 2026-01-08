package com.benchpress200.photique.exhibition.infrastructure.persistence.elasticsearch.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import com.benchpress200.photique.exhibition.domain.enumeration.Target;
import com.benchpress200.photique.exhibition.infrastructure.persistence.elasticsearch.ExhibitionSearchRepositoryCustom;
import com.benchpress200.photique.singlework.infrastructure.exception.ElasticsearchMaxResultWindowException;
import com.benchpress200.photique.singlework.infrastructure.exception.ElasticsearchSearchException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ExhibitionSearchRepositoryImpl implements ExhibitionSearchRepositoryCustom {
    private final static String INDEX_NAME = "exhibitions";
    private final static String WRITER_FIELD = "writerNickname";
    private final static String TITLE_FIELD = "title";
    private final static String DESCRIPTION_FIELD = "description";
    private final static String TAGS_FIELD = "tags";
    private final static int MAX_RESULT_WINDOW = 10_000;

    private final ElasticsearchClient elasticsearchClient;

    /*
     * === bool 복합 쿼리 ===
     * must: 쿼리가 참인 도큐먼트들 검색
     * must_not: 쿼리가 거짓인 도큐먼트들 검색
     * should: 검색 결과 중 이 쿼리에 해당하는 도큐먼트의 점수 높임
     * filter: 쿼리가 참인 도큐먼트를 검색하지만 스코어를 계산 X (must 보다 검색 속도가 빠르고 캐싱 가능)
     *
     * - 각각의 bool 쿼리안에 match 혹은 match_phrase 로 쿼리 구성
     * - 각각의 bool 쿼리들은 AND 연산자와 유사하게 동작함을 인지
     */
    @Override
    public Page<ExhibitionSearch> searchExhibition(
            Target target,
            String keyword,
            Pageable pageable
    ) {
        Query targetQuery = createTargetQuery(target, keyword);
        Query tagQuery = createTagQuery(keyword);
        SortOptions sortOptions = createSortOptions(pageable);
        Query query = createFinalQuery(targetQuery, tagQuery);

        int from = (int) pageable.getOffset();
        int size = pageable.getPageSize();

        if (from + size > MAX_RESULT_WINDOW) {
            throw new ElasticsearchMaxResultWindowException();
        }

        try {
            SearchResponse<ExhibitionSearch> response = elasticsearchClient.search(s -> s
                            .index(INDEX_NAME)
                            .query(query)
                            .sort(sortOptions)
                            .from(from)
                            .size(size),
                    ExhibitionSearch.class
            );

            return createResultPage(response, pageable);
        } catch (Exception e) {
            String message = e.getMessage();
            throw new ElasticsearchSearchException(message);
        }
    }

    private Page<ExhibitionSearch> createResultPage(SearchResponse<ExhibitionSearch> response, Pageable pageable) {
        HitsMetadata<ExhibitionSearch> exhibitionSearchHitsMetadata = response.hits();
        List<ExhibitionSearch> contents = exhibitionSearchHitsMetadata.hits().stream()
                .map(Hit::source)
                .toList();

        long totalElements = exhibitionSearchHitsMetadata.total() != null
                ? exhibitionSearchHitsMetadata.total().value()
                : contents.size();

        return new PageImpl<>(
                contents,
                pageable,
                totalElements
        );
    }

    private Query createFinalQuery(Query keywordQuery, Query tagQuery) {
        return Query.of(q -> q
                .bool(b -> {
                    b.must(keywordQuery);
                    b.should(tagQuery);

                    return b;
                })
        );
    }

    private SortOptions createSortOptions(Pageable pageable) {
        Sort.Order order = pageable.getSort()
                .iterator()
                .next();
        String field = order.getProperty();

        SortOrder sortOrder = order.isAscending() ? SortOrder.Asc : SortOrder.Desc;

        return SortOptions.of(s -> s
                .field(f -> f
                        .field(field)
                        .order(sortOrder)
                )
        );
    }

    private Query createTagQuery(String keyword) {
        // 키워드가 없다면 전체 검색
        if (keyword == null || keyword.isBlank()) {
            return MatchAllQuery.of(m -> m)
                    ._toQuery();
        }

        return MatchQuery.of(m -> m
                .field(TAGS_FIELD)
                .query(keyword)
        )._toQuery();
    }

    private Query createTargetQuery(
            Target target,
            String keyword
    ) {
        // 키워드가 없다면 전체 검색
        if (keyword == null || keyword.isBlank()) {
            return MatchAllQuery.of(m -> m)
                    ._toQuery();
        }

        // 작가 이름 검색이라면
        if (target.equals(Target.WRITER)) {
            return MatchQuery.of(m -> m
                            .field(WRITER_FIELD)
                            .query(keyword)
                    )
                    ._toQuery();
        }

        // 작품 검색이라면 제목과 설명에서 키워드 검색
        return MultiMatchQuery.of(m -> m
                .fields(TITLE_FIELD, DESCRIPTION_FIELD)
                .query(keyword)
        )._toQuery();
    }
}
