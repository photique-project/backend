package com.benchpress200.photique.singlework.infrastructure;


import com.benchpress200.photique.singlework.domain.entity.QSingleWork;
import com.benchpress200.photique.singlework.domain.entity.QSingleWorkComment;
import com.benchpress200.photique.singlework.domain.entity.QSingleWorkLike;
import com.benchpress200.photique.singlework.domain.entity.QSingleWorkTag;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import com.benchpress200.photique.tag.domain.entity.QTag;
import com.benchpress200.photique.user.domain.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
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
    public SingleWork findPopularSingleWork() {
        // 이번주 월요일
        LocalDateTime startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay();
        // 이번주 일요일
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);

        QSingleWork singleWork = QSingleWork.singleWork;
        QSingleWorkLike like = QSingleWorkLike.singleWorkLike;

        List<SingleWork> popularSingleWorks = queryFactory
                .select(singleWork)
                .from(like)
                .join(like.singleWork, singleWork)
                .where(like.createdAt.between(startOfWeek, endOfWeek)) // 이번 주 기간 내 좋아요 데이터만 조회
                .groupBy(singleWork) // 작품별로 그룹핑
                .orderBy(like.count().desc()) // 좋아요 개수가 많은 순으로 정렬
                .limit(1) // 인기작품 한 개만 셀렉
                .fetch();

        if (popularSingleWorks.isEmpty()) {
            return queryFactory
                    .selectFrom(singleWork)
                    .orderBy(singleWork.createdAt.desc()) // 최신 작품 기준 정렬
                    .fetchFirst(); // 가장 최신 작품 하나만 가져오기
        }

        return popularSingleWorks.get(0);
    }


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
        long totalCount = results.size();

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
                            NumberExpression<Long> likeCount = Expressions.numberTemplate(Long.class,
                                    "count({0})", QSingleWorkLike.singleWorkLike.id);
                            return new OrderSpecifier<>(direction, likeCount);
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
