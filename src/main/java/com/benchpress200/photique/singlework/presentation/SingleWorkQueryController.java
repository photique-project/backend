package com.benchpress200.photique.singlework.presentation;

import com.benchpress200.photique.common.constant.PathVariableName;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.singlework.application.SingleWorkQueryService;
import com.benchpress200.photique.singlework.application.result.SingleWorkDetailsResult;
import com.benchpress200.photique.singlework.presentation.constant.SingleWorkResponseMessage;
import com.benchpress200.photique.singlework.presentation.response.SingleWorkDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URL.BASE_URL + URL.SINGLE_WORK_DOMAIN)
@RequiredArgsConstructor
public class SingleWorkQueryController {
    private final SingleWorkQueryService singleWorkQueryService;

    @GetMapping(URL.SINGLE_WORK_DATA)
    public ResponseEntity<?> getSingleWorkDetails(
            @PathVariable(PathVariableName.SINGLEWORK_ID) Long singleworkId
    ) {
        SingleWorkDetailsResult singleWorkDetailsResult = singleWorkQueryService.getSingleWorkDetails(singleworkId);
        SingleWorkDetailsResponse singleWorkDetailsResponse = SingleWorkDetailsResponse.from(singleWorkDetailsResult);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                SingleWorkResponseMessage.WORK_FETCH_SUCCESS,
                singleWorkDetailsResponse
        );
    }
}
