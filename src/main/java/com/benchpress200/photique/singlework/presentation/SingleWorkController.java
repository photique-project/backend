package com.benchpress200.photique.singlework.presentation;

import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ApiSuccessResponse;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.singlework.application.SingleWorkService;
import com.benchpress200.photique.singlework.domain.dto.LikedSingleWorkRequest;
import com.benchpress200.photique.singlework.domain.dto.LikedSingleWorkResponse;
import com.benchpress200.photique.singlework.domain.dto.MySingleWorkRequest;
import com.benchpress200.photique.singlework.domain.dto.MySingleWorkResponse;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkCreateRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkDetailRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkDetailResponse;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkLikeDecrementRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkLikeIncrementRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkSearchRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkSearchResponse;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URL.BASE_URL + URL.SINGLE_WORK_DOMAIN)
@RequiredArgsConstructor
public class SingleWorkController {
    private final SingleWorkService singleWorkService;

    @PostMapping
    public ApiSuccessResponse<?> postNewSingleWork(
            @ModelAttribute @Valid final SingleWorkCreateRequest singleWorkCreateRequest
    ) {
        singleWorkService.postNewSingleWork(singleWorkCreateRequest);
        return ResponseHandler.handleSuccessResponse(HttpStatus.CREATED);
    }


    @GetMapping(URL.SINGLE_WORK_DATA)
    public ApiSuccessResponse<?> getSingleWorkDetails(
            @ModelAttribute final SingleWorkDetailRequest singleWorkDetailRequest,
            @PathVariable final Long singleworkId
    ) {
        singleWorkDetailRequest.withSingleWorkId(singleworkId);
        SingleWorkDetailResponse singleWorkDetailResponse = singleWorkService.getSingleWorkDetails(
                singleWorkDetailRequest);
        return ResponseHandler.handleSuccessResponse(singleWorkDetailResponse, HttpStatus.OK);
    }


    @GetMapping
    public ApiSuccessResponse<?> searchSingleWorks(
            @ModelAttribute @Valid final SingleWorkSearchRequest singleWorkSearchRequest,
            final Pageable pageable
    ) {
        Page<SingleWorkSearchResponse> singleWorkSearchPage = singleWorkService.searchSingleWorks(
                singleWorkSearchRequest,
                pageable
        );
        return ResponseHandler.handleSuccessResponse(singleWorkSearchPage, HttpStatus.OK);
    }

    @PatchMapping(URL.SINGLE_WORK_DATA)
    public ApiSuccessResponse<?> updateSingleWorkDetails(
            @PathVariable final Long singleworkId,
            @RequestBody @Valid final SingleWorkUpdateRequest singleWorkUpdateRequest
    ) {
        singleWorkUpdateRequest.withSingleWorkId(singleworkId);
        singleWorkService.updateSingleWorkDetails(singleWorkUpdateRequest);
        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(URL.SINGLE_WORK_DATA)
    public ApiSuccessResponse<?> removeSingleWork(
            @PathVariable final Long singleworkId
    ) {
        singleWorkService.removeSingleWork(singleworkId);
        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @PostMapping(URL.SINGLE_WORK_DATA + URL.LIKE)
    public ApiSuccessResponse<?> incrementLike(
            @PathVariable final Long singleworkId,
            @RequestBody final SingleWorkLikeIncrementRequest singleWorkLikeIncrementRequest
    ) {
        singleWorkLikeIncrementRequest.withSingleWorkId(singleworkId);
        singleWorkService.incrementLike(singleWorkLikeIncrementRequest);
        return ResponseHandler.handleSuccessResponse(HttpStatus.CREATED);
    }

    @DeleteMapping(URL.SINGLE_WORK_DATA + URL.LIKE)
    public ApiSuccessResponse<?> decrementLike(
            @PathVariable final Long singleworkId,
            @RequestBody final SingleWorkLikeDecrementRequest singleWorkLikeDecrementRequest
    ) {
        singleWorkLikeDecrementRequest.withSingleWorkId(singleworkId);
        singleWorkService.decrementLike(singleWorkLikeDecrementRequest);
        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @GetMapping(URL.LIKE)
    public ApiSuccessResponse<?> getLikedSingleWorks(
            @ModelAttribute final LikedSingleWorkRequest likedSingleWorkRequest,
            final Pageable pageable
    ) {
        Page<LikedSingleWorkResponse> likedSingleWorkPage = singleWorkService.getLikedSingleWorks(
                likedSingleWorkRequest,
                pageable
        );

        return ResponseHandler.handleSuccessResponse(likedSingleWorkPage, HttpStatus.OK);
    }

    @GetMapping(URL.MY_DATA)
    public ApiSuccessResponse<?> getMySingleWorks(
            @ModelAttribute final MySingleWorkRequest mySingleWorkRequest,
            final Pageable pageable
    ) {
        Page<MySingleWorkResponse> mySingleWorkResponsePage = singleWorkService.getMySingleWorks(mySingleWorkRequest,
                pageable);

        return ResponseHandler.handleSuccessResponse(mySingleWorkResponsePage, HttpStatus.OK);
    }
}
