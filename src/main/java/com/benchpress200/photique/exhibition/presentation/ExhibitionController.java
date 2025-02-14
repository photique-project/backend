package com.benchpress200.photique.exhibition.presentation;

import com.benchpress200.photique.auth.interceptor.Auth;
import com.benchpress200.photique.auth.interceptor.OwnResource;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ApiSuccessResponse;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.exhibition.application.ExhibitionService;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionCreateRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionDetailResponse;
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
@RequestMapping(URL.BASE_URL + URL.EXHIBITION_DOMAIN)
@RequiredArgsConstructor
public class ExhibitionController {

    private final ExhibitionService exhibitionService;

    @Auth
    @OwnResource
    @PostMapping
    public ApiSuccessResponse<?> createNewExhibition(
            @ModelAttribute @Valid final ExhibitionCreateRequest exhibitionCreateRequest
    ) {
        exhibitionService.createNewExhibition(exhibitionCreateRequest);
        return ResponseHandler.handleSuccessResponse(HttpStatus.CREATED);
    }

    @Auth
    @GetMapping(URL.EXHIBITION_DATA)
    public ApiSuccessResponse<?> getExhibitionDetail(
            @PathVariable final Long exhibitionId
    ) {
        ExhibitionDetailResponse exhibitionDetailResponse = exhibitionService.getExhibitionDetail(exhibitionId);
        return ResponseHandler.handleSuccessResponse(exhibitionDetailResponse, HttpStatus.OK);
    }
}
