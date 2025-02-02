package com.benchpress200.photique.singlework.infrastructure;

import com.benchpress200.photique.singlework.domain.entity.QSingleWork;
import com.benchpress200.photique.singlework.domain.entity.QSingleWorkTag;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.user.domain.entity.QUser;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class SingleWorkRepositoryImpl implements SingleWorkRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SingleWork> searchWorks(
            String q,
            String target,
            String sort,
            List<String> categories,
            Pageable pageable
    ) {
        QSingleWork singleWork = QSingleWork.singleWork;
        QSingleWorkTag singleWorkTag = QSingleWorkTag.singleWorkTag;
        QUser user = QUser.user;

        // 기본 검색 조건

        BooleanExpression condition = singleWork.isNotNull();

        return null;

    }
}
