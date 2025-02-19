package com.benchpress200.photique.user.infrastructure;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery.Builder;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.benchpress200.photique.user.domain.entity.UserSearch;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

@RequiredArgsConstructor
public class UserSearchRepositoryCustomImpl implements UserSearchRepositoryCustom {
    private final ElasticsearchClient elasticsearchClient;

    @Override
    public Page<UserSearch> search(
            final String keyword,
            final Pageable pageable
    ) {
        try {
            // Bool Query 빌드
            Builder keywordBoolQuery = QueryBuilders.bool();

            if (!keyword.isEmpty()) {
                keywordBoolQuery.should(new MatchQuery.Builder()
                        .field("nickname")
                        .query(keyword)
                        .fuzziness("AUTO")
                        .build()._toQuery());

            }
            keywordBoolQuery.minimumShouldMatch("1");

            // 정렬 적용
            Sort sort = pageable.getSort();
            Order order = sort.stream()
                    .findFirst().orElseThrow();
            String property = order.getProperty();
            SortOrder sortOrder = order.isAscending() ? SortOrder.Asc : SortOrder.Desc;
            SortOptions sortOptions = SortOptions.of(s -> s.field(f -> f.field(property).order(sortOrder)));

            // 검색 실행
            SearchResponse<UserSearch> searchResponse = elasticsearchClient.search(s -> s
                            .index("users")
                            .query(keywordBoolQuery.build()._toQuery())
                            .sort(sortOptions)
                            .from((int) pageable.getOffset())
                            .size(pageable.getPageSize()),
                    UserSearch.class
            );

            // 결과 매핑
            List<UserSearch> results = searchResponse.hits().hits().stream()
                    .map(Hit::source)
                    .toList();

            return new PageImpl<>(results, pageable, searchResponse.hits().total().value());
        } catch (Exception e) {
            throw new RuntimeException("Elasticsearch 검색 중 오류 발생", e);
        }
    }
}
