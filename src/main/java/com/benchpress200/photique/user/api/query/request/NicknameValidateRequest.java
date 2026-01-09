package com.benchpress200.photique.user.api.query.request;

import com.benchpress200.photique.user.application.query.model.NicknameValidateQuery;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NicknameValidateRequest {
    @NotNull(message = "Invalid nickname")
    @Pattern(regexp = "^[^\\s]{1,11}$", message = "Invalid nickname")
    private String nickname;

    public NicknameValidateQuery toQuery() {
        return NicknameValidateQuery.builder()
                .nickname(nickname)
                .build();
    }
}
