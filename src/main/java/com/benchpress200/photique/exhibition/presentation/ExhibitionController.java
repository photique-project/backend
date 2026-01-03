package com.benchpress200.photique.exhibition.presentation;

import com.benchpress200.photique.common.constant.ApiPath;
import com.benchpress200.photique.common.response.ApiSuccessResponse;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.exhibition.application.ExhibitionService;
import com.benchpress200.photique.exhibition.domain.dto.BookmarkedExhibitionRequest;
import com.benchpress200.photique.exhibition.domain.dto.BookmarkedExhibitionResponse;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionBookmarkRemoveRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionBookmarkRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionCreateRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionDetailsRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionDetailsResponse;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExhibitionController {
    private final ExhibitionService exhibitionService;

    @PostMapping(
            path = ApiPath.EXHIBITION_ROOT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ApiSuccessResponse<?> holdNewExhibition(
            @ModelAttribute @Valid ExhibitionCreateRequest exhibitionCreateRequest
    ) {
        exhibitionService.holdNewExhibition(exhibitionCreateRequest);
        return ResponseHandler.handleSuccessResponse(HttpStatus.CREATED);
    }

    @GetMapping(ApiPath.EXHIBITION_DATA)
    public ApiSuccessResponse<?> getExhibitionDetails(
            @ModelAttribute ExhibitionDetailsRequest exhibitionDetailsRequest,
            @PathVariable Long exhibitionId
    ) {
        exhibitionDetailsRequest.withExhibitionId(exhibitionId);
        ExhibitionDetailsResponse exhibitionDetailsResponse = exhibitionService.getExhibitionDetails(
                exhibitionDetailsRequest);
        return ResponseHandler.handleSuccessResponse(exhibitionDetailsResponse, HttpStatus.OK);
    }

    @GetMapping(ApiPath.EXHIBITION_ROOT)
    public ApiSuccessResponse<?> searchExhibitions(
            @ModelAttribute @Valid ExhibitionSearchRequest exhibitionSearchRequest,
            Pageable pageable
    ) {
        Page<ExhibitionSearchResponse> exhibitionSearchPage = exhibitionService.searchExhibitions(
                exhibitionSearchRequest,
                pageable
        );

        return ResponseHandler.handleSuccessResponse(exhibitionSearchPage, HttpStatus.OK);
    }

    @DeleteMapping(ApiPath.EXHIBITION_DATA)
    public ApiSuccessResponse<?> removeExhibition(
            @PathVariable Long exhibitionId
    ) {
        exhibitionService.removeExhibition(exhibitionId);
        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @PostMapping(ApiPath.EXHIBITION_LIKE)
    public ApiSuccessResponse<?> incrementLike(
            @PathVariable Long exhibitionId,
            @RequestBody ExhibitionLikeIncrementRequest exhibitionLikeIncrementRequest
    ) {
        exhibitionLikeIncrementRequest.withExhibitionId(exhibitionId);
        exhibitionService.incrementLike(exhibitionLikeIncrementRequest);
        return ResponseHandler.handleSuccessResponse(HttpStatus.CREATED);
    }

    @DeleteMapping(ApiPath.EXHIBITION_LIKE)
    public ApiSuccessResponse<?> decrementLike(
            @PathVariable Long exhibitionId,
            @RequestBody ExhibitionLikeDecrementRequest exhibitionLikeDecrementRequest
    ) {
        exhibitionLikeDecrementRequest.withExhibitionId(exhibitionId);
        exhibitionService.decrementLike(exhibitionLikeDecrementRequest);
        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @PostMapping(ApiPath.EXHIBITION_BOOKMARK)
    public ApiSuccessResponse<?> addBookmark(
            @PathVariable Long exhibitionId,
            @RequestBody ExhibitionBookmarkRequest exhibitionBookmarkRequest

    ) {
        exhibitionBookmarkRequest.withExhibitionId(exhibitionId);
        exhibitionService.addBookmark(exhibitionBookmarkRequest);
        return ResponseHandler.handleSuccessResponse(HttpStatus.CREATED);
    }

    @DeleteMapping(ApiPath.EXHIBITION_BOOKMARK)
    public ApiSuccessResponse<?> removeBookmark(
            @PathVariable Long exhibitionId,
            @RequestBody ExhibitionBookmarkRemoveRequest exhibitionBookmarkRemoveRequest
    ) {
        exhibitionBookmarkRemoveRequest.withExhibitionId(exhibitionId);
        exhibitionService.removeBookmark(exhibitionBookmarkRemoveRequest);

        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @GetMapping(ApiPath.EXHIBITION_MY_BOOKMARK)
    public ApiSuccessResponse<?> getBookmarkedExhibitions(
            @ModelAttribute BookmarkedExhibitionRequest bookmarkedExhibitionRequest,
            Pageable pageable
    ) {
        Page<BookmarkedExhibitionResponse> bookmaredExhibitionPage = exhibitionService.getBookmarkedExhibitions(
                bookmarkedExhibitionRequest, pageable);
        return ResponseHandler.handleSuccessResponse(bookmaredExhibitionPage, HttpStatus.OK);
    }

    @GetMapping(ApiPath.EXHIBITION_MY_LIKE)
    public ApiSuccessResponse<?> getLikedExhibitions(
            @ModelAttribute LikedExhibitionRequest likedExhibitionRequest,
            Pageable pageable
    ) {
        Page<LikedExhibitionResponse> likedExhibitionRequestPage = exhibitionService.getLikedExhibitions(
                likedExhibitionRequest, pageable);

        return ResponseHandler.handleSuccessResponse(likedExhibitionRequestPage, HttpStatus.OK);
    }

    @GetMapping(ApiPath.EXHIBITION_MY_DATA)
    public ApiSuccessResponse<?> getMyExhibitions(
            @ModelAttribute MyExhibitionRequest myExhibitionRequest,
            Pageable pageable
    ) {
        Page<MyExhibitionResponse> myExhibitionResponsePage = exhibitionService.getMyExhibitions(
                myExhibitionRequest,
                pageable
        );

        return ResponseHandler.handleSuccessResponse(myExhibitionResponsePage, HttpStatus.OK);
    }
}
