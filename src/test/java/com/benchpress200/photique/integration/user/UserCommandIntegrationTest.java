package com.benchpress200.photique.integration.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.application.command.port.out.persistence.AuthMailCodeCommandPort;
import com.benchpress200.photique.auth.domain.entity.AuthMailCode;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.common.api.constant.MultipartKey;
import com.benchpress200.photique.integration.auth.support.fixture.AuthMailCodeFixture;
import com.benchpress200.photique.support.base.BaseIntegrationTest;
import com.benchpress200.photique.support.fixture.MultipartJsonFixture;
import com.benchpress200.photique.user.api.command.support.fixture.ResisterRequestFixture;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

@DisplayName("유저 커맨드 API 통합 테스트")
public class UserCommandIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private UserQueryPort userQueryPort;

    @Autowired
    private AuthMailCodeCommandPort authMailCodeCommandPort;

    @Test
    @DisplayName("회원가입 요청 시 요청이 유효하면 DB에 회원을 저장하고 201을 반환한다")
    public void register_whenRequestIsValid() throws Exception {
        // given
        ResisterRequestFixture request = ResisterRequestFixture.builder().build();
        MockMultipartFile userPart = MultipartJsonFixture.builder()
                .key(MultipartKey.USER)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        String nickname = request.getNickname();
        String email = request.getEmail();

        AuthMailCode authMailCode = AuthMailCodeFixture.builder()
                .email(email)
                .isVerified(true)
                .build();

        authMailCodeCommandPort.save(authMailCode);

        // when
        ResultActions resultActions = requestRegister(userPart, null);
        Optional<User> user = userQueryPort.findByEmailAndDeletedAtIsNull(email);

        // then
        resultActions
                .andExpect(status().isCreated());

        Assertions.assertThat(user)
                .isPresent()
                .get()
                .satisfies(u -> {
                    Assertions.assertThat(u.getNickname()).isEqualTo(nickname);
                    Assertions.assertThat(u.getEmail()).isEqualTo(email);
                });
    }


    private ResultActions requestRegister(
            MockMultipartFile userPart,
            MockMultipartFile profileImagePart
    ) throws Exception {
        MockMultipartHttpServletRequestBuilder multipartBuilder = multipart(ApiPath.USER_ROOT)
                .file(userPart);

        if (profileImagePart != null) {
            multipartBuilder = multipartBuilder.file(profileImagePart);
        }

        MockHttpServletRequestBuilder httpBuilder = multipartBuilder.contentType(MediaType.MULTIPART_FORM_DATA);

        return mockMvc.perform(httpBuilder);
    }
}
