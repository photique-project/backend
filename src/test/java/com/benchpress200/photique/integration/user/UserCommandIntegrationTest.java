package com.benchpress200.photique.integration.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.application.command.port.out.persistence.AuthMailCodeCommandPort;
import com.benchpress200.photique.auth.domain.entity.AuthMailCode;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.common.api.constant.MultipartKey;
import com.benchpress200.photique.integration.auth.support.fixture.AuthMailCodeFixture;
import com.benchpress200.photique.support.base.BaseIntegrationTest;
import com.benchpress200.photique.support.fixture.MultipartFileFixture;
import com.benchpress200.photique.support.fixture.MultipartJsonFixture;
import com.benchpress200.photique.user.api.command.support.fixture.ResisterRequestFixture;
import com.benchpress200.photique.user.application.command.port.out.persistence.UserCommandPort;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.support.UserFixture;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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

    @Autowired
    private UserCommandPort userCommandPort;

    @AfterEach
    void cleanUp() {
        authMailCodeCommandPort.deleteAll();
        userCommandPort.deleteAll();
    }

    @Nested
    @DisplayName("회원가입")
    class ResisterTest {
        @Test
        @DisplayName("요청이 유효하면 회원을 저장하고 201을 반환한다")
        public void whenRequestValid() throws Exception {
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
            resultActions.andExpect(status().isCreated());
            Assertions.assertThat(user)
                    .isPresent()
                    .get()
                    .satisfies(u -> {
                        Assertions.assertThat(u.getNickname()).isEqualTo(nickname);
                        Assertions.assertThat(u.getEmail()).isEqualTo(email);
                    });
        }

        @ParameterizedTest
        @DisplayName("이메일이 유효하지 않으면 가입을 진행하지 않고 400을 반환한다")
        @MethodSource("com.benchpress200.photique.integration.user.UserCommandIntegrationTest#invalidEmails")
        public void whenEmailInvalid(String invalidEmail) throws Exception {
            // given
            ResisterRequestFixture request = ResisterRequestFixture.builder()
                    .email(invalidEmail)
                    .build();

            String email = request.getEmail();

            MockMultipartFile userPart = MultipartJsonFixture.builder()
                    .key(MultipartKey.USER)
                    .object(request)
                    .objectMapper(objectMapper)
                    .build();

            // when
            ResultActions resultActions = requestRegister(userPart, null);
            Optional<User> user = userQueryPort.findByEmailAndDeletedAtIsNull(email);

            // then
            resultActions.andExpect(status().isBadRequest());
            Assertions.assertThat(user).isNotPresent();
        }

        @ParameterizedTest
        @DisplayName("비밀번호가 유효하지 않으면 가입을 진행하지 않고 400을 반환한다")
        @MethodSource("com.benchpress200.photique.integration.user.UserCommandIntegrationTest#invalidPasswords")
        public void whenPasswordInvalid(String invalidPassword) throws Exception {
            // given
            ResisterRequestFixture request = ResisterRequestFixture.builder()
                    .password(invalidPassword)
                    .build();

            String email = request.getEmail();

            MockMultipartFile userPart = MultipartJsonFixture.builder()
                    .key(MultipartKey.USER)
                    .object(request)
                    .objectMapper(objectMapper)
                    .build();

            // when
            ResultActions resultActions = requestRegister(userPart, null);
            Optional<User> user = userQueryPort.findByEmailAndDeletedAtIsNull(email);

            // then
            resultActions.andExpect(status().isBadRequest());
            Assertions.assertThat(user).isNotPresent();
        }

        @ParameterizedTest
        @DisplayName("닉네임이 유효하지 않으면 가입을 진행하지 않고 400을 반환한다")
        @MethodSource("com.benchpress200.photique.integration.user.UserCommandIntegrationTest#invalidNicknames")
        public void whenNicknameInvalid(String invalidNickname) throws Exception {
            // given
            ResisterRequestFixture request = ResisterRequestFixture.builder()
                    .nickname(invalidNickname)
                    .build();

            MockMultipartFile userPart = MultipartJsonFixture.builder()
                    .key(MultipartKey.USER)
                    .object(request)
                    .objectMapper(objectMapper)
                    .build();

            String email = request.getEmail();

            // when
            ResultActions resultActions = requestRegister(userPart, null);
            Optional<User> user = userQueryPort.findByEmailAndDeletedAtIsNull(email);

            // then
            resultActions.andExpect(status().isBadRequest());
            Assertions.assertThat(user).isNotPresent();
        }

        @ParameterizedTest
        @DisplayName("프로필 사진이 유효하지 않으면 가입을 진행하지 않고 400을 반환한다")
        @MethodSource("com.benchpress200.photique.integration.user.UserCommandIntegrationTest#invalidProfileImages")
        public void whenProfileImageInvalid(MockMultipartFile invalidProfileImage) throws Exception {
            // given
            ResisterRequestFixture request = ResisterRequestFixture.builder().build();
            MockMultipartFile userPart = MultipartJsonFixture.builder()
                    .key(MultipartKey.USER)
                    .object(request)
                    .objectMapper(objectMapper)
                    .build();

            String email = request.getEmail();

            // when
            ResultActions resultActions = requestRegister(userPart, invalidProfileImage);
            Optional<User> user = userQueryPort.findByEmailAndDeletedAtIsNull(email);

            // then
            resultActions.andExpect(status().isBadRequest());
            Assertions.assertThat(user).isNotPresent();
        }

        @Test
        @DisplayName("이메일 인증 코드가 존재하지 않거나 만료되었다면 가입을 진행하지 않고 410을 반환한다")
        public void whenAuthMailCodeExpired() throws Exception {
            // given
            ResisterRequestFixture request = ResisterRequestFixture.builder().build();
            MockMultipartFile userPart = MultipartJsonFixture.builder()
                    .key(MultipartKey.USER)
                    .object(request)
                    .objectMapper(objectMapper)
                    .build();

            String email = request.getEmail();

            // when
            ResultActions resultActions = requestRegister(userPart, null);
            Optional<User> user = userQueryPort.findByEmailAndDeletedAtIsNull(email);

            // then
            resultActions.andExpect(status().isGone());
            Assertions.assertThat(user).isNotPresent();
        }

        @Test
        @DisplayName("이메일 인증을 진행하지 않았다면 가입을 진행하지 않고 401을 반환한다")
        public void whenAuthMailCodeNotVerified() throws Exception {
            // given
            ResisterRequestFixture request = ResisterRequestFixture.builder().build();
            MockMultipartFile userPart = MultipartJsonFixture.builder()
                    .key(MultipartKey.USER)
                    .object(request)
                    .objectMapper(objectMapper)
                    .build();

            String email = request.getEmail();

            AuthMailCode authMailCode = AuthMailCodeFixture.builder()
                    .email(email)
                    .build();

            authMailCodeCommandPort.save(authMailCode);

            // when
            ResultActions resultActions = requestRegister(userPart, null);
            Optional<User> user = userQueryPort.findByEmailAndDeletedAtIsNull(email);

            // then
            resultActions.andExpect(status().isUnauthorized());
            Assertions.assertThat(user).isNotPresent();
        }

        @Test
        @DisplayName("중복된 이메일이 존재한다면 가입을 진행하지 않고 409을 반환한다")
        public void whenEmailDuplicated() throws Exception {
            // given
            ResisterRequestFixture request = ResisterRequestFixture.builder().build();
            MockMultipartFile userPart = MultipartJsonFixture.builder()
                    .key(MultipartKey.USER)
                    .object(request)
                    .objectMapper(objectMapper)
                    .build();

            String email = request.getEmail();

            AuthMailCode authMailCode = AuthMailCodeFixture.builder()
                    .email(email)
                    .isVerified(true)
                    .build();

            authMailCodeCommandPort.save(authMailCode);

            User user = UserFixture.builder()
                    .email(email)
                    .build();

            userCommandPort.save(user);

            // when
            ResultActions resultActions = requestRegister(userPart, null);

            // then
            resultActions.andExpect(status().isConflict());
        }

        @Test
        @DisplayName("중복된 닉네임이 존재한다면 가입을 진행하지 않고 409을 반환한다")
        public void whenNicknameDuplicated() throws Exception {
            // given
            ResisterRequestFixture request = ResisterRequestFixture.builder().build();
            MockMultipartFile userPart = MultipartJsonFixture.builder()
                    .key(MultipartKey.USER)
                    .object(request)
                    .objectMapper(objectMapper)
                    .build();

            String email = request.getEmail();
            String nickname = request.getNickname();

            AuthMailCode authMailCode = AuthMailCodeFixture.builder()
                    .email(email)
                    .isVerified(true)
                    .build();

            authMailCodeCommandPort.save(authMailCode);

            User user = UserFixture.builder()
                    .nickname(nickname)
                    .build();

            userCommandPort.save(user);

            // when
            ResultActions resultActions = requestRegister(userPart, null);

            // then
            resultActions.andExpect(status().isConflict());
        }
    }

    private static Stream<MockMultipartFile> invalidProfileImages() {
        MockMultipartFile emptyImage = MultipartFileFixture.builder()
                .key(MultipartKey.PROFILE_IMAGE)
                .fileName("profile.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .build();

        MockMultipartFile bigImage = MultipartFileFixture.builder()
                .key(MultipartKey.PROFILE_IMAGE)
                .fileName("profile.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[5 * 1024 * 1024 + 1])
                .build();

        MockMultipartFile noNameImage = MultipartFileFixture.builder()
                .key(MultipartKey.PROFILE_IMAGE)
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[1])
                .build();

        MockMultipartFile gifImage = MultipartFileFixture.builder()
                .key(MultipartKey.PROFILE_IMAGE)
                .fileName("profile.gif")
                .contentType(MediaType.IMAGE_GIF_VALUE)
                .content(new byte[1])
                .build();

        return Stream.of(
                emptyImage,     // 빈 파일
                bigImage,       // 5MB 초과
                noNameImage,    // 파일명 없음
                gifImage        // 허용되지 않는 확장자
        );
    }

    private static Stream<String> invalidNicknames() {
        return Stream.of(
                null,               // @NotNull 위반
                "nick name",        // 공백 포함
                "a".repeat(12)      // 12자 (최댓값 초과)
        );
    }

    private static Stream<String> invalidPasswords() {
        return Stream.of(
                null,           // @NotNull 위반
                "password!",    // 숫자 없음
                "pass1!",       // 8자 미만
                "12345678!"     // 영문자 없음
        );
    }

    private static Stream<String> invalidEmails() {
        return Stream.of(
                null,           // @NotNull 위반
                "invalid-email" // 이메일 형식 위반
        );
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
