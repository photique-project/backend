package com.benchpress200.photique.singlework.application.query.service;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.singlework.application.query.model.LikedSingleWorkSearchQuery;
import com.benchpress200.photique.singlework.application.query.port.in.SearchLikedSingleWorkUseCase;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkLikeQueryPort;
import com.benchpress200.photique.singlework.application.query.result.LikedSingleWorkSearchResult;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkLike;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SingleWorkLikeQueryService implements
        SearchLikedSingleWorkUseCase {
    private final AuthenticationUserProviderPort authenticationUserProvider;

    private final SingleWorkLikeQueryPort singleWorkLikeQueryPort;

    @Override
    public LikedSingleWorkSearchResult searchLikedSingleWork(LikedSingleWorkSearchQuery query) {
        // 본인 좋아요 작품이나 본인 작품 검색 시 한 페이지에 최대 30개
        // 작품이 들어가는데 각각의 데이터에 대한 좋아요 수 카운팅을 위해 집계 쿼리가
        // 나가는 것을 방지하기 위해 작품 엔티티 칼럼에 좋아요 카운트 수 추가하여 비정규화
        Long userId = authenticationUserProvider.getCurrentUserId();
        String keyword = query.getKeyword();
        Pageable pageable = query.getPageable();

        Page<SingleWorkLike> singleWorkLikePage = singleWorkLikeQueryPort.searchLikedSingleWorkByDeletedAtIsNull(
                userId,
                keyword,
                pageable
        );

        return LikedSingleWorkSearchResult.from(singleWorkLikePage);
    }
}
