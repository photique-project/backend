package com.benchpress200.photique.user.presentation;

import com.benchpress200.photique.common.response.ResponseBody;
import com.benchpress200.photique.user.presentation.request.JoinRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

public interface UserCommandControllerDocs {
    // 회원가입
    @Operation(
            summary = "회원가입",
            description = "요청 데이터로 회원가입을 진행합니다."
    )
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseBody.class),
                            examples = {
                                    @ExampleObject(
                                            value = "{ \"status\": 201, \"message\": \"Join completed\", \"data\": null, \"timestamp\": \"2025-08-27T13:50:08.002Z\" }"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 파라미터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseBody.class),
                            examples = {
                                    @ExampleObject(
                                            value = "{ \"status\": 400, \"message\": \"Invalid {}\", \"data\": null, \"timestamp\": \"2025-08-27T13:50:08.002Z\" }"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 에러",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseBody.class),
                            examples = {
                                    @ExampleObject(
                                            value = "{ \"status\": 500, \"message\": \"Server Error\", \"data\": null, \"timestamp\": \"2025-08-27T13:50:08.002Z\" }"
                                    )
                            }
                    )
            )
    })
    ResponseEntity<?> join(
            @Parameter(description = "유저 입력 데이터") @RequestPart("user") @Valid JoinRequest joinRequest,
            @Parameter(description = "유저 입력 프로필 이미지 (필수값이 아니며, 5MB이하의 jpg/jpeg/png 파일만 가능합니다.)") @RequestPart(value = "profileImage", required = false) MultipartFile profileImages
    );
}
