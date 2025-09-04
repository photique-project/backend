package com.benchpress200.photique.user.presentation;

import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ResponseBody;
import com.benchpress200.photique.user.presentation.request.ValidateNicknameRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "User API", description = "유저 도메인 API 입니다.")
@RequestMapping(URL.BASE_URL + URL.USER_DOMAIN)
public interface UserQueryControllerDocs {

    /**
     * 닉네임 중복 검사 API
     */
    @GetMapping(URL.VALIDATE_NICKNAME)
    @Operation(
            summary = "닉네임 중복 검사",
            description = "닉네임 중복 검사를 진행합니다.."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "닉네임 중복 검사 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseBody.class),
                            examples = {
                                    @ExampleObject(
                                            value = "{ \"status\": 200, \"message\": \"Nickname duplication check completed\", \"data\": {\"isDuplicated\": true}, \"timestamp\": \"YYYY-MM-DDThh:mm:ss\" }"
                                    ) // 데이터 isDuplicated로 넣자
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 닉네임",
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
    ResponseEntity<?> validateNickname(
            @Parameter(description = "닉네임 중복 검사") ValidateNicknameRequest validateNicknameRequest
    );
}
