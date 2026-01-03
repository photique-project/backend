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
        Long userId = authenticationUserProvider.getCurrentUserId();
        String keyword = query.getKeyword();
        Pageable pageable = query.getPageable();

        Page<SingleWorkLike> likedSingleWorkPage = singleWorkLikeQueryPort.searchLikedSingleWork(
                userId,
                keyword,
                pageable
        );
        
        return LikedSingleWorkSearchResult.from(likedSingleWorkPage);
    }
}
