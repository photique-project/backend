package com.benchpress200.photique.integration.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.application.command.port.out.persistence.AuthMailCodeCommandPort;
import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.entity.AuthMailCode;
import com.benchpress200.photique.auth.domain.vo.AuthenticationTokens;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.common.api.constant.MultipartKey;
import com.benchpress200.photique.integration.auth.support.fixture.AuthMailCodeFixture;
import com.benchpress200.photique.support.base.BaseIntegrationTest;
import com.benchpress200.photique.support.fixture.MultipartFileFixture;
import com.benchpress200.photique.support.fixture.MultipartJsonFixture;
import com.benchpress200.photique.user.api.command.support.fixture.ResisterRequestFixture;
import com.benchpress200.photique.user.api.command.support.fixture.UserDetailsUpdateRequestFixture;
import com.benchpress200.photique.user.application.command.port.out.persistence.UserCommandPort;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.support.UserFixture;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

    @Autowired
    private AuthenticationTokenManagerPort authenticationTokenManagerPort;

    private User savedUser;
    private String accessToken;

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

    @Nested
    @DisplayName("유저 정보 수정")
    class UpdateUserDetailsTest {
        @BeforeEach
        void setUp() {
            savedUser = userCommandPort.save(UserFixture.builder().build());
            AuthenticationTokens tokens = authenticationTokenManagerPort.issueTokens(
                    savedUser.getId(),
                    savedUser.getRole().name()
            );
            accessToken = tokens.getAccessToken();
        }

        @Test
        @DisplayName("요청이 유효하면 유저 정보를 수정하고 204를 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            UserDetailsUpdateRequestFixture request = UserDetailsUpdateRequestFixture.builder().build();
            MockMultipartFile userPart = MultipartJsonFixture.builder()
                    .key(MultipartKey.USER)
                    .object(request)
                    .objectMapper(objectMapper)
                    .build();

            // when
            ResultActions resultActions = requestUpdateUserDetailsAuthenticated(savedUser.getId(), userPart, null);
            Optional<User> updatedUser = userQueryPort.findByIdAndDeletedAtIsNull(savedUser.getId());

            // then
            resultActions.andExpect(status().isNoContent());
            Assertions.assertThat(updatedUser)
                    .isPresent()
                    .get()
                    .satisfies(u -> {
                        Assertions.assertThat(u.getNickname()).isEqualTo(request.getNickname());
                        Assertions.assertThat(u.getIntroduction()).isEqualTo(request.getIntroduction());
                    });
        }

        @Test
        @DisplayName("인증 토큰이 없으면 401을 반환한다")
        public void whenNotAuthenticated() throws Exception {
            // given
            UserDetailsUpdateRequestFixture request = UserDetailsUpdateRequestFixture.builder().build();
            MockMultipartFile userPart = MultipartJsonFixture.builder()
                    .key(MultipartKey.USER)
                    .object(request)
                    .objectMapper(objectMapper)
                    .build();

            // when
            ResultActions resultActions = requestUpdateUserDetails(savedUser.getId(), userPart, null);

            // then
            resultActions.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("다른 유저의 userId로 수정을 요청하면 403을 반환한다")
        public void whenAnotherUserForbidden() throws Exception {
            // given
            User otherUser = userCommandPort.save(UserFixture.builder()
                    .email("other@example.com")
                    .nickname("다른유저")
                    .build());
            UserDetailsUpdateRequestFixture request = UserDetailsUpdateRequestFixture.builder().build();
            MockMultipartFile userPart = MultipartJsonFixture.builder()
                    .key(MultipartKey.USER)
                    .object(request)
                    .objectMapper(objectMapper)
                    .build();

            // when
            ResultActions resultActions = requestUpdateUserDetailsAuthenticated(otherUser.getId(), userPart, null);

            // then
            resultActions.andExpect(status().isForbidden());
        }

        @ParameterizedTest
        @DisplayName("닉네임이 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.integration.user.UserCommandIntegrationTest#invalidUpdateNicknames")
        public void whenNicknameInvalid(String invalidNickname) throws Exception {
            // given
            UserDetailsUpdateRequestFixture request = UserDetailsUpdateRequestFixture.builder()
                    .nickname(invalidNickname)
                    .build();
            MockMultipartFile userPart = MultipartJsonFixture.builder()
                    .key(MultipartKey.USER)
                    .object(request)
                    .objectMapper(objectMapper)
                    .build();

            // when
            ResultActions resultActions = requestUpdateUserDetailsAuthenticated(savedUser.getId(), userPart, null);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("한 줄 소개가 50자를 초과하면 400을 반환한다")
        public void whenIntroductionTooLong() throws Exception {
            // given
            String invalidIntroduction = "가".repeat(51);
            UserDetailsUpdateRequestFixture request = UserDetailsUpdateRequestFixture.builder()
                    .introduction(invalidIntroduction)
                    .build();
            MockMultipartFile userPart = MultipartJsonFixture.builder()
                    .key(MultipartKey.USER)
                    .object(request)
                    .objectMapper(objectMapper)
                    .build();

            // when
            ResultActions resultActions = requestUpdateUserDetailsAuthenticated(savedUser.getId(), userPart, null);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @DisplayName("프로필 사진이 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.integration.user.UserCommandIntegrationTest#invalidUpdateProfileImages")
        public void whenProfileImageInvalid(MockMultipartFile invalidProfileImage) throws Exception {
            // given
            UserDetailsUpdateRequestFixture request = UserDetailsUpdateRequestFixture.builder().build();
            MockMultipartFile userPart = MultipartJsonFixture.builder()
                    .key(MultipartKey.USER)
                    .object(request)
                    .objectMapper(objectMapper)
                    .build();

            // when
            ResultActions resultActions = requestUpdateUserDetailsAuthenticated(savedUser.getId(), userPart, invalidProfileImage);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("유저가 존재하지 않으면 404를 반환한다")
        public void whenUserNotFound() throws Exception {
            // given
            userCommandPort.deleteAll();
            UserDetailsUpdateRequestFixture request = UserDetailsUpdateRequestFixture.builder().build();
            MockMultipartFile userPart = MultipartJsonFixture.builder()
                    .key(MultipartKey.USER)
                    .object(request)
                    .objectMapper(objectMapper)
                    .build();

            // when
            ResultActions resultActions = requestUpdateUserDetailsAuthenticated(savedUser.getId(), userPart, null);

            // then
            resultActions.andExpect(status().isNotFound());
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

    private static Stream<String> invalidUpdateNicknames() {
        return Stream.of(
                "nick name",        // 공백 포함
                "a".repeat(12)      // 12자 (최댓값 초과)
        );
    }

    private static Stream<MockMultipartFile> invalidUpdateProfileImages() {
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
                bigImage,       // 5MB 초과
                noNameImage,    // 파일명 없음
                gifImage        // 허용되지 않는 확장자
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

    private ResultActions requestUpdateUserDetails(
            Long userId,
            MockMultipartFile userPart,
            MockMultipartFile profileImagePart
    ) throws Exception {
        MockMultipartHttpServletRequestBuilder multipartBuilder = multipart(HttpMethod.PATCH, ApiPath.USER_DATA, userId)
                .file(userPart);

        if (profileImagePart != null) {
            multipartBuilder = multipartBuilder.file(profileImagePart);
        }

        return mockMvc.perform(multipartBuilder.contentType(MediaType.MULTIPART_FORM_DATA));
    }

    private ResultActions requestUpdateUserDetailsAuthenticated(
            Long userId,
            MockMultipartFile userPart,
            MockMultipartFile profileImagePart
    ) throws Exception {
        MockMultipartHttpServletRequestBuilder multipartBuilder = multipart(HttpMethod.PATCH, ApiPath.USER_DATA, userId)
                .file(userPart);

        if (profileImagePart != null) {
            multipartBuilder = multipartBuilder.file(profileImagePart);
        }

        return mockMvc.perform(
                multipartBuilder
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        );
    }
}
