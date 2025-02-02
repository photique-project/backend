package com.benchpress200.photique.singlework.presentation;

import com.benchpress200.photique.auth.interceptor.Auth;
import com.benchpress200.photique.auth.interceptor.OwnResource;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ApiSuccessResponse;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.singlework.application.SingleWorkService;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkCreateRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkDetailResponse;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkSearchRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URL.BASE_URL + URL.SINGLE_WORK_DOMAIN)
@RequiredArgsConstructor
public class SingleWorkController {

    private final SingleWorkService singleWorkService;

    @Auth
    @OwnResource
    @PostMapping
    public ApiSuccessResponse<?> createNewSingleWork(
            @ModelAttribute @Valid final SingleWorkCreateRequest singleWorkCreateRequest
    ) {
        singleWorkService.createNewSingleWork(singleWorkCreateRequest);
        return ResponseHandler.handleSuccessResponse(HttpStatus.CREATED);
    }

    @GetMapping
    public ApiSuccessResponse<?> getSingleWorks(
            @ModelAttribute @Valid final SingleWorkSearchRequest singleWorkSearchRequest
    ) {
        singleWorkService.getSingleWorks(singleWorkSearchRequest);
        return null;
    }

    @GetMapping(URL.SINGLE_WORK_DATA)
    public ApiSuccessResponse<?> getSingleWorkDetail(
            @PathVariable final Long singleworkId
    ) {
        SingleWorkDetailResponse singleWorkDetailResponse = singleWorkService.getSingleWorkDetail(singleworkId);
        return ResponseHandler.handleSuccessResponse(singleWorkDetailResponse, HttpStatus.OK);
    }
}
