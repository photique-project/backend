package com.benchpress200.photique.singlework.infrastructure;

import com.benchpress200.photique.common.domain.entity.QTag;
import com.benchpress200.photique.singlework.domain.entity.QSingleWork;
import com.benchpress200.photique.singlework.domain.entity.QSingleWorkComment;
import com.benchpress200.photique.singlework.domain.entity.QSingleWorkTag;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import com.benchpress200.photique.user.domain.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
public class SingleWorkRepositoryImpl implements SingleWorkRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SingleWork> searchSingleWorks(
            final Target target,
            final List<String> keywords,
            final List<Category> categories,
            final Pageable pageable
    ) {

        QSingleWork sw = QSingleWork.singleWork;
        QSingleWorkTag swt = QSingleWorkTag.singleWorkTag;
        QTag t = QTag.tag;
        QSingleWorkComment swc = QSingleWorkComment.singleWorkComment;
        QUser user = QUser.user;

        BooleanBuilder whereClause = new BooleanBuilder();

        // 키워드 검색 조건 추가
        if (!keywords.isEmpty()) {

            BooleanBuilder keywordCondition = new BooleanBuilder();
            // 작품검색
            if (target.equals(Target.WORK)) {
                for (String keyword : keywords) {
                    keywordCondition.or(sw.title.containsIgnoreCase(keyword))
                            .or(t.name.in(keywords)); // 태그 이름 검색
                }
            }

            //작가검색
            if (target.equals(Target.WRITER)) {
                for (String keyword : keywords) {
                    keywordCondition.or(user.nickname.containsIgnoreCase(keyword));
                }
            }

            whereClause.and(keywordCondition);
        }

        // 카테고리 필터링 추가
        if (!categories.isEmpty()) {
            whereClause.and(sw.category.in(categories));
        }

        // 쿼리 실행
        JPAQuery<Tuple> query = queryFactory
                .select(sw, Expressions.numberTemplate(Long.class, "COUNT({0})", swc.id))
                .from(sw)
                .join(swt).on(sw.id.eq(swt.singleWork.id))
                .join(t).on(swt.tag.id.eq(t.id))
                .leftJoin(swc).on(sw.id.eq(swc.singleWork.id))
                .groupBy(sw.id, t.id)
                .where(whereClause)
                .orderBy(getSortOrder(sw, pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<SingleWork> results = query.fetch().stream()
                .map(tuple -> tuple.get(sw))
                .distinct()
                .toList();

        // 전체 개수 조회
        // 이것만 확인하면 나머지 디비로 조회하는 로직완성
        // 이후 엘라스틱 조회 로직 정리하고 업데이트 삭제도 확실하게 하고
        // 엘라스틱 서치로 조회하는 로직작성하고 테스트 후에 성능비교 진행하고 노션 작성
        long totalCount = queryFactory
                .select(sw.count())
                .from(sw)
                .where(whereClause)
                .fetchOne();

        return new PageImpl<>(results, pageable, totalCount);

    }

    private OrderSpecifier<?> getSortOrder(QSingleWork sw, Pageable pageable) {
        Sort sort = pageable.getSort();

        return sort.stream()
                .findFirst()
                .map(order -> {
                    String property = order.getProperty();
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;

                    switch (property) {
                        case "createdAt":
                            return new OrderSpecifier<>(direction, sw.createdAt);
                        case "likeCount":
                            return new OrderSpecifier<>(direction, sw.likeCount);
                        case "viewCount":
                            return new OrderSpecifier<>(direction, sw.viewCount);
                        case "commentCount":
                            NumberExpression<Long> commentCount = Expressions.numberTemplate(Long.class,
                                    "count({0})", QSingleWorkComment.singleWorkComment.id);
                            return new OrderSpecifier<>(direction, commentCount);
                        default:
                            return new OrderSpecifier<>(Order.DESC, sw.createdAt);
                    }
                })
                .orElse(new OrderSpecifier<>(Order.DESC, sw.createdAt));
    }
}
