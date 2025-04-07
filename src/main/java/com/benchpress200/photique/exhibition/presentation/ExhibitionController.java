package com.benchpress200.photique.exhibition.presentation;

import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.interceptor.Auth;
import com.benchpress200.photique.common.interceptor.OwnResource;
import com.benchpress200.photique.common.response.ApiSuccessResponse;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.exhibition.application.ExhibitionService;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URL.BASE_URL + URL.EXHIBITION_DOMAIN)
@RequiredArgsConstructor
public class ExhibitionController {

    private final ExhibitionService exhibitionService;

    @Auth
    @OwnResource
    @PostMapping
    public ApiSuccessResponse<?> holdNewExhibition(
            @ModelAttribute @Valid final ExhibitionCreateRequest exhibitionCreateRequest
    ) {
        exhibitionService.holdNewExhibition(exhibitionCreateRequest);
        return ResponseHandler.handleSuccessResponse(HttpStatus.CREATED);
    }

    @Auth
    @GetMapping(URL.EXHIBITION_DATA)
    public ApiSuccessResponse<?> getExhibitionDetail(
            @ModelAttribute final ExhibitionDetailRequest exhibitionDetailRequest,
            @PathVariable final Long exhibitionId
    ) {
        exhibitionDetailRequest.withExhibitionId(exhibitionId);
        ExhibitionDetailResponse exhibitionDetailResponse = exhibitionService.getExhibitionDetail(
                exhibitionDetailRequest);
        return ResponseHandler.handleSuccessResponse(exhibitionDetailResponse, HttpStatus.OK);
    }

    @GetMapping
    public ApiSuccessResponse<?> searchExhibitions(
            @ModelAttribute @Valid final ExhibitionSearchRequest exhibitionSearchRequest,
            final Pageable pageable
    ) {
        Page<ExhibitionSearchResponse> exhibitionSearchPage = exhibitionService.searchExhibitions(
                exhibitionSearchRequest,
                pageable
        );

        return ResponseHandler.handleSuccessResponse(exhibitionSearchPage, HttpStatus.OK);
    }

    @Auth
    @OwnResource
    @DeleteMapping(URL.EXHIBITION_DATA)
    public ApiSuccessResponse<?> removeExhibition(
            @PathVariable final Long exhibitionId
    ) {
        exhibitionService.removeExhibition(exhibitionId);
        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @Auth
    @OwnResource
    @PostMapping(URL.EXHIBITION_DATA + URL.LIKE)
    public ApiSuccessResponse<?> incrementLike(
            @PathVariable final Long exhibitionId,
            @RequestBody final ExhibitionLikeIncrementRequest exhibitionLikeIncrementRequest
    ) {
        exhibitionLikeIncrementRequest.withExhibitionId(exhibitionId);
        exhibitionService.incrementLike(exhibitionLikeIncrementRequest);
        return ResponseHandler.handleSuccessResponse(HttpStatus.CREATED);
    }

    @Auth
    @OwnResource
    @DeleteMapping(URL.EXHIBITION_DATA + URL.LIKE)
    public ApiSuccessResponse<?> decrementLike(
            @PathVariable final Long exhibitionId,
            @RequestBody final ExhibitionLikeDecrementRequest exhibitionLikeDecrementRequest
    ) {
        exhibitionLikeDecrementRequest.withExhibitionId(exhibitionId);
        exhibitionService.decrementLike(exhibitionLikeDecrementRequest);
        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @Auth
    @OwnResource
    @PostMapping(URL.EXHIBITION_DATA + URL.BOOKMARK)
    public ApiSuccessResponse<?> addBookmark(
            @PathVariable final Long exhibitionId,
            @RequestBody final ExhibitionBookmarkRequest exhibitionBookmarkRequest

    ) {
        exhibitionBookmarkRequest.withExhibitionId(exhibitionId);
        exhibitionService.addBookmark(exhibitionBookmarkRequest);
        return ResponseHandler.handleSuccessResponse(HttpStatus.CREATED);
    }

    @Auth
    @OwnResource
    @DeleteMapping(URL.EXHIBITION_DATA + URL.BOOKMARK)
    public ApiSuccessResponse<?> removeBookmark(
            @PathVariable final Long exhibitionId,
            @RequestBody final ExhibitionBookmarkRemoveRequest exhibitionBookmarkRemoveRequest
    ) {
        exhibitionBookmarkRemoveRequest.withExhibitionId(exhibitionId);
        exhibitionService.removeBookmark(exhibitionBookmarkRemoveRequest);

        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @Auth
    @GetMapping(URL.BOOKMARK)
    public ApiSuccessResponse<?> getBookmarkedExhibitions(
            @ModelAttribute final BookmarkedExhibitionRequest bookmarkedExhibitionRequest,
            final Pageable pageable
    ) {
        Page<BookmarkedExhibitionResponse> bookmaredExhibitionPage = exhibitionService.getBookmarkedExhibitions(
                bookmarkedExhibitionRequest, pageable);
        return ResponseHandler.handleSuccessResponse(bookmaredExhibitionPage, HttpStatus.OK);
    }

    @Auth
    @GetMapping(URL.LIKE)
    public ApiSuccessResponse<?> getLikedExhibitions(
            @ModelAttribute final LikedExhibitionRequest likedExhibitionRequest,
            final Pageable pageable
    ) {
        Page<LikedExhibitionResponse> likedExhibitionRequestPage = exhibitionService.getLikedExhibitions(
                likedExhibitionRequest, pageable);

        return ResponseHandler.handleSuccessResponse(likedExhibitionRequestPage, HttpStatus.OK);
    }

    @Auth
    @GetMapping(URL.WHO_AM_I)
    public ApiSuccessResponse<?> getMyExhibitions(
            @ModelAttribute final MyExhibitionRequest myExhibitionRequest,
            final Pageable pageable
    ) {
        Page<MyExhibitionResponse> myExhibitionResponsePage = exhibitionService.getMyExhibitions(
                myExhibitionRequest,
                pageable
        );

        return ResponseHandler.handleSuccessResponse(myExhibitionResponsePage, HttpStatus.OK);
    }
}
