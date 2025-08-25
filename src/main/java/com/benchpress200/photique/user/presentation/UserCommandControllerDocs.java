package com.benchpress200.photique.user.presentation;

import com.benchpress200.photique.user.presentation.request.JoinRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

public interface UserCommandControllerDocs {
    @Operation(
            summary = "회원가입",
            description = "요청 데이터로 회원가입을 진행합니다."
    )
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공")
    })
    ResponseEntity<?> join(
            @Parameter(description = "유저 입력 데이터") @RequestPart("user") @Valid JoinRequest joinRequest,
            @Parameter(description = "유저 입력 프로필 이미지 (필수값이 아니며, 5MB이하의 jpg/jpeg/png 파일만 가능합니다.)") @RequestPart(value = "profileImage", required = false) MultipartFile profileImages
    );
}
