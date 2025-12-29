package com.benchpress200.photique.user.api.query.request;

import com.benchpress200.photique.user.application.query.model.NicknameValidateQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NicknameValidateRequest {
    @NotNull(message = "Invalid nickname")
    @Pattern(regexp = "^[^\\s]{1,11}$", message = "Invalid nickname")
    @Schema(description = "1글자 이상 11글자 이하만 가능하며 공백을 포함할 수 없습니다.", example = "nickname")
    private String nickname;

    public NicknameValidateQuery toQuery() {
        return NicknameValidateQuery.builder()
                .nickname(nickname)
                .build();
    }
}
