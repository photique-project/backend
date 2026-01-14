package com.benchpress200.photique.exhibition.application.query.service;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.common.application.support.Ids;
import com.benchpress200.photique.exhibition.application.query.model.BookmarkedExhibitionSearchQuery;
import com.benchpress200.photique.exhibition.application.query.port.in.SearchBookmarkedExhibitionUseCase;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionBookmarkQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionLikeQueryPort;
import com.benchpress200.photique.exhibition.application.query.result.BookmarkedExhibitionSearchResult;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionBookmark;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExhibitionBookmarkQueryService implements
        SearchBookmarkedExhibitionUseCase {
    private final AuthenticationUserProviderPort authenticationUserProviderPort;
    private final ExhibitionBookmarkQueryPort exhibitionBookmarkQueryPort;
    private final ExhibitionLikeQueryPort exhibitionLikeQueryPort;

    @Override
    public BookmarkedExhibitionSearchResult searchBookmarkedExhibition(BookmarkedExhibitionSearchQuery query) {
        Long userId = authenticationUserProviderPort.getCurrentUserId();
        String keyword = query.getKeyword();
        Pageable pageable = query.getPageable();

        Page<ExhibitionBookmark> exhibitionBookmarkPage = exhibitionBookmarkQueryPort.searchBookmarkedExhibitionByDeletedAtIsNull(
                userId,
                keyword,
                pageable
        );

        // 검색한 작품 좋아요 여부 확인
        List<Long> exhibitionIds = exhibitionBookmarkPage.stream()
                .map(exhibitionBookmark -> exhibitionBookmark.getExhibition().getId())
                .toList();

        Ids likedExhibitionIds = findLikedExhibitionIds(userId, exhibitionIds);

        return BookmarkedExhibitionSearchResult.of(exhibitionBookmarkPage, likedExhibitionIds);
    }

    private Ids findLikedExhibitionIds(Long userId, List<Long> exhibitionIds) {
        Set<Long> likedSet = exhibitionLikeQueryPort.findExhibitionIds(userId, exhibitionIds);

        return Ids.from(likedSet);
    }
}
