package com.benchpress200.photique.singlework.infrastructure;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery.Builder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;


@RequiredArgsConstructor
public class SingleWorkSearchRepositoryCustomImpl implements SingleWorkSearchRepositoryCustom {
    private final ElasticsearchClient elasticsearchClient;

    @Override
    public Page<SingleWorkSearch> search(
            final Target target,
            final List<String> keywords,
            final List<Category> categories,
            final Pageable pageable
    ) {
        try {
            // Bool Query 빌드
            // query.bool내부에 must, must_not, should, filter 옵션가능
            Builder keywordBoolQuery = QueryBuilders.bool();

            // 키워드 검색 조건 추가
            if (!keywords.isEmpty()) {
                List<Query> shouldQuery = new ArrayList<>();

                if (target.equals(Target.WORK)) {
                    for (String keyword : keywords) {
                        shouldQuery.add(new TermQuery.Builder().field("title").value(keyword).build()._toQuery());
                        shouldQuery.add(new TermQuery.Builder().field("tags").value(keyword).build()._toQuery());
                    }
                }

                if (target.equals(Target.WRITER)) {
                    for (String keyword : keywords) {
                        shouldQuery.add(
                                new TermQuery.Builder().field("writerNickname").value(keyword).build()._toQuery());
                    }
                }

                keywordBoolQuery.should(shouldQuery);
            }

            // 카테고리 필터링 추가
            Builder categoryBoolQuery = QueryBuilders.bool();

            if (!categories.isEmpty()) {
                List<Query> shouldQuery = new ArrayList<>();
                List<String> categoryNames = categories.stream()
                        .map(Category::getValue)
                        .toList();

                for (String categoryName : categoryNames) {
                    shouldQuery.add(new TermQuery.Builder().field("category").value(categoryName).build()._toQuery());
                }

                categoryBoolQuery.should(shouldQuery);
                categoryBoolQuery.minimumShouldMatch("1");
            }

            Builder finalBoolQuery = QueryBuilders.bool();
            finalBoolQuery.must(keywordBoolQuery.build()._toQuery());
            finalBoolQuery.must(categoryBoolQuery.build()._toQuery());

            // 정렬 적용
            Sort sort = pageable.getSort();
            Order order = sort.stream()
                    .findFirst().orElseThrow();
            String property = order.getProperty();
            SortOrder sortOrder;
            if (order.isAscending()) {
                sortOrder = SortOrder.Asc;
            } else {
                sortOrder = SortOrder.Desc;
            }
            SortOptions sortOptions = SortOptions.of(s -> s.field(f -> f.field(property).order(sortOrder)));

            // 검색 실행
            SearchResponse<SingleWorkSearch> searchResponse = elasticsearchClient.search(s -> s
                            .index("singleworks")
                            .query(finalBoolQuery.build()._toQuery())
                            .sort(sortOptions)
                            .from((int) pageable.getOffset())
                            .size(pageable.getPageSize()),
                    SingleWorkSearch.class
            );

            // 결과 매핑
            List<SingleWorkSearch> results = searchResponse.hits().hits().stream()
                    .map(Hit::source)
                    .toList();

            return new PageImpl<>(results, pageable, searchResponse.hits().total().value());

        } catch (Exception e) {
            throw new RuntimeException("Elasticsearch 검색 중 오류 발생", e);
        }
    }
}
