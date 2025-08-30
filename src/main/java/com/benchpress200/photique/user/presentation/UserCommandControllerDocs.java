package com.benchpress200.photique.user.presentation;

import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.interceptor.Auth;
import com.benchpress200.photique.common.response.ResponseBody;
import com.benchpress200.photique.user.presentation.request.JoinRequest;
import com.benchpress200.photique.user.presentation.request.UpdateUserDetailsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

public interface UserCommandControllerDocs {
    // 회원가입
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "회원가입",
            description = "요청 데이터로 회원가입을 진행합니다."
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
                                            value = "{ \"status\": 201, \"message\": \"Join completed\", \"data\": null, \"timestamp\": \"YYYY-MM-DDThh:mm:ss\" }"
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
                                            value = "{ \"status\": 400, \"message\": \"Invalid {}\", \"data\": null, \"timestamp\": \"YYYY-MM-DDThh:mm:ss\" }"
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
                                            value = "{ \"status\": 500, \"message\": \"Server Error\", \"data\": null, \"timestamp\": \"YYYY-MM-DDThh:mm:ss\" }"
                                    )
                            }
                    )
            )
    })
    ResponseEntity<?> join(
            @Parameter(description = "유저 입력 데이터") JoinRequest joinRequest,
            @Parameter(description = "유저 입력 프로필 이미지 (필수값이 아니며, 5MB이하의 jpg/jpeg/png 파일만 가능합니다.)") MultipartFile profileImage
    );

    // 유저 정보 수정
    @Auth
    @PatchMapping(
            path = URL.USER_DATA,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "유저 정보 수정",
            description = "요청 데이터로 유저 정보를 수정합니다.."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "유저 정보 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseBody.class),
                            examples = {
                                    @ExampleObject(
                                            value = "{ \"status\": 204, \"message\": \"User details updated\", \"data\": null, \"timestamp\": \"YYYY-MM-DDThh:mm:ss\" }"
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
                                            value = "{ \"status\": 400, \"message\": \"Invalid {}\", \"data\": null, \"timestamp\": \"YYYY-MM-DDThh:mm:ss\" }"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseBody.class),
                            examples = {
                                    @ExampleObject(
                                            value = "{ \"status\": 400, \"message\": \"Authentication failed\", \"data\": null, \"timestamp\": \"YYYY-MM-DDThh:mm:ss\" }"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "유효하지 않은 접근",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseBody.class),
                            examples = {
                                    @ExampleObject(
                                            value = "{ \"status\": 400, \"message\": \"Access denied\", \"data\": null, \"timestamp\": \"YYYY-MM-DDThh:mm:ss\" }"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 유저",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseBody.class),
                            examples = {
                                    @ExampleObject(
                                            value = "{ \"status\": 400, \"message\": \"User with ID [id] not found\", \"data\": null, \"timestamp\": \"YYYY-MM-DDThh:mm:ss\" }"
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
                                            value = "{ \"status\": 500, \"message\": \"Server Error\", \"data\": null, \"timestamp\": \"YYYY-MM-DDThh:mm:ss\" }"
                                    )
                            }
                    )
            )
    })
    ResponseEntity<?> updateUserDetails(
            @Parameter(description = "유저 id") Long userId,
            @Parameter(description = "유저 업데이트 데이터") UpdateUserDetailsRequest updateUserDetailsRequest,
            @Parameter(description = "유저 입력 프로필 이미지 (필수값이 아니며, 5MB이하의 jpg/jpeg/png 파일만 가능합니다.)") MultipartFile profileImage
    );
}
