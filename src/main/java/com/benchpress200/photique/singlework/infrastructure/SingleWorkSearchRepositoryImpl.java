package com.benchpress200.photique.singlework.infrastructure;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import com.benchpress200.photique.singlework.domain.repository.SingleWorkSearchRepositoryCustom;
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
public class SingleWorkSearchRepositoryImpl implements SingleWorkSearchRepositoryCustom {
    private final static String INDEX_NAME = "singleworks";
    private final static String WRITER_FIELD = "writerNickname";
    private final static String TITLE_FIELD = "title";
    private final static String DESCRIPTION_FIELD = "description";
    private final static String TAGS_FIELD = "tags";
    private final static String CATEGORY_FIELD = "category";
    private final static String MINIMUM_CATEGORY_MATCHING = "1";
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
    public Page<SingleWorkSearch> search(
            Target target,
            String keyword,
            List<Category> categories,
            Pageable pageable
    ) {
        // 검색 프로세스 정리하고 쿼리작성
        // 1. 검색 타겟 설정
        // 2. 키워드 null 이거나 비었거나 공백만 있으면 작품 전체 검색
        // 3. 카테고리없으면 전체 검색 or 있으면 해당 카테고리에 속하는 애들만 검색
        // 4. 정렬 기준대로 페이지 조회

        // target : must filter
        // 키워드: must match
        // 카테고리: must match
        // 정렬 기준 대로 정렬하고 페이징
        Query targetQuery = createTargetQuery(target, keyword);
        Query tagQuery = createTagQuery(keyword);
        List<Query> categoriesQuery = createCategoriesQuery(categories);
        SortOptions sortOptions = createSortOptions(pageable);

        Query query = createFinalQuery(
                targetQuery,
                tagQuery,
                categoriesQuery
        );

        // ES는 기본적으로 내부에서 from + size값이 10,000 초과하는 데이터를 조회하지 못하도록 함
        // => 각 샤드에서 조건에 맞는 문서를 검색하고 합친 다음에 다시 정렬하고 from을 시작점으로 해서
        // => size만큼 조회해야 하는데, 결과적으로 오프셋 기반 즉시 점프가 불가능하고 수많은 데이터를 읽고
        // => ES의 메모리 상에서 정렬과정을 거쳐야하므로 뒤쪽 오프셋으로 갈수록 실제 조회 데이터보다
        // => 버리는 데이터가 엄청 커지면서 부담이 커지기 때문에 생긴 제약 조건
        // => scroll 이나 after search 방식으로 deep paging 문제 해결 가능 !

        // 하지만, 일반 서비스 검색 UI에서는 from(333) + size(30) <= 10,000의 페이지 제한은
        // 문제가 없다고 판단되고, 더 깊은 페이징을 진행하면 ES의 JVM 부하가 심해질 것으로 예상되어
        // 조건 검사 후 예외 처리
        int from = (int) pageable.getOffset();
        int size = pageable.getPageSize();

        if (from + size > MAX_RESULT_WINDOW) {
            throw new ElasticsearchMaxResultWindowException();
        }

        try {
            SearchResponse<SingleWorkSearch> response = elasticsearchClient.search(s -> s
                            .index(INDEX_NAME)
                            .query(query)
                            .sort(sortOptions)
                            .from(from)
                            .size(size),
                    SingleWorkSearch.class
            );

            return createResultPage(response, pageable);
        } catch (Exception e) {
            String message = e.getMessage();
            throw new ElasticsearchSearchException(message);
        }
    }

    private Page<SingleWorkSearch> createResultPage(SearchResponse<SingleWorkSearch> response, Pageable pageable) {
        HitsMetadata<SingleWorkSearch> singleWorkSearchHitsMetadata = response.hits();
        List<SingleWorkSearch> contents = singleWorkSearchHitsMetadata.hits().stream()
                .map(Hit::source)
                .toList();

        long totalElements = singleWorkSearchHitsMetadata.total() != null
                ? singleWorkSearchHitsMetadata.total().value()
                : contents.size();

        return new PageImpl<>(
                contents,
                pageable,
                totalElements
        );
    }

    private Query createFinalQuery(
            Query keywordQuery,
            Query tagQuery,
            List<Query> categoriesQuery
    ) {
        return Query.of(q -> q
                .bool(b -> {
                    // 키워드에 해당하는 타이틀,
                    b.must(keywordQuery);
                    b.should(tagQuery);
                    b.filter(f -> f
                            .bool(cb -> cb
                                    .should(categoriesQuery)
                                    .minimumShouldMatch(MINIMUM_CATEGORY_MATCHING)
                            )
                    );

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

    private List<Query> createCategoriesQuery(List<Category> categories) {
        // 지정한 카테고리가 없다면 전체 검색
        if (categories == null || categories.isEmpty()) {
            return List.of(
                    MatchAllQuery.of(m -> m)
                            ._toQuery()
            );
        }

        return categories.stream()
                .map(category ->
                        TermQuery.of(t -> t
                                .field(CATEGORY_FIELD)
                                .value(category.name())
                        )._toQuery()
                )
                .toList();
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
