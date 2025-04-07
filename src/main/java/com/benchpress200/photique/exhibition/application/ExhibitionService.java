package com.benchpress200.photique.exhibition.application;

import com.benchpress200.photique.exhibition.domain.dto.BookmarkedExhibitionRequest;
import com.benchpress200.photique.exhibition.domain.dto.BookmarkedExhibitionResponse;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionBookmarkRemoveRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionBookmarkRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionCreateRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionDetailRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionDetailResponse;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionLikeDecrementRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionLikeIncrementRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionSearchRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionSearchResponse;
import com.benchpress200.photique.exhibition.domain.dto.LikedExhibitionRequest;
import com.benchpress200.photique.exhibition.domain.dto.LikedExhibitionResponse;
import com.benchpress200.photique.exhibition.domain.dto.MyExhibitionRequest;
import com.benchpress200.photique.exhibition.domain.dto.MyExhibitionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExhibitionService {
    void holdNewExhibition(ExhibitionCreateRequest exhibitionCreateRequest);

    ExhibitionDetailResponse getExhibitionDetail(ExhibitionDetailRequest exhibitionDetailRequest);

    Page<ExhibitionSearchResponse> searchExhibitions(ExhibitionSearchRequest exhibitionSearchRequest,
                                                     Pageable pageable);

    void removeExhibition(Long exhibitionId);

    void incrementLike(ExhibitionLikeIncrementRequest exhibitionLikeIncrementRequest);

    void decrementLike(ExhibitionLikeDecrementRequest exhibitionLikeDecrementRequest);

    void addBookmark(ExhibitionBookmarkRequest exhibitionBookmarkRequest);

    void removeBookmark(ExhibitionBookmarkRemoveRequest exhibitionBookmarkRemoveRequest);

    Page<BookmarkedExhibitionResponse> getBookmarkedExhibitions(BookmarkedExhibitionRequest bookmarkedExhibitionRequest,
                                                                Pageable pageable);

    Page<LikedExhibitionResponse> getLikedExhibitions(LikedExhibitionRequest likedExhibitionRequest, Pageable pageable);

    Page<MyExhibitionResponse> getMyExhibitions(MyExhibitionRequest myExhibitionRequest, Pageable pageable);
}
