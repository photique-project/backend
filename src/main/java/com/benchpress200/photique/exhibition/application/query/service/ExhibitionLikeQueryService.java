package com.benchpress200.photique.exhibition.application.query.service;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.common.application.support.Ids;
import com.benchpress200.photique.exhibition.application.query.model.LikedExhibitionSearchQuery;
import com.benchpress200.photique.exhibition.application.query.port.in.SearchLikedExhibitionUseCase;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionBookmarkQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionLikeQueryPort;
import com.benchpress200.photique.exhibition.application.query.result.LikedExhibitionSearchResult;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionLike;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExhibitionLikeQueryService implements
        SearchLikedExhibitionUseCase {
    private final AuthenticationUserProviderPort authenticationUserProviderPort;
    private final ExhibitionBookmarkQueryPort exhibitionBookmarkQueryPort;
    private final ExhibitionLikeQueryPort exhibitionLikeQueryPort;

    @Override
    public LikedExhibitionSearchResult searchLikedExhibition(LikedExhibitionSearchQuery query) {
        Long userId = authenticationUserProviderPort.getCurrentUserId();
        String keyword = query.getKeyword();
        Pageable pageable = query.getPageable();

        Page<ExhibitionLike> exhibitionLikePage = exhibitionLikeQueryPort.searchLikedExhibitionByDeletedAtIsNull(
                userId,
                keyword,
                pageable
        );

        // 검색한 작품 북마크 여부 확인
        List<Long> exhibitionIds = exhibitionLikePage.stream()
                .map(exhibitionLike -> exhibitionLike.getExhibition().getId())
                .toList();

        Ids bookmarkedExhibitionIds = findBookmarkedExhibitionIds(userId, exhibitionIds);

        return LikedExhibitionSearchResult.of(exhibitionLikePage, bookmarkedExhibitionIds);
    }

    private Ids findBookmarkedExhibitionIds(Long userId, List<Long> ids) {
        Set<Long> bookmarkedSet = exhibitionBookmarkQueryPort.findExhibitionIds(userId, ids);

        return Ids.from(bookmarkedSet);
    }
}
