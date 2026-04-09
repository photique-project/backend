package com.benchpress200.photique.user.api.command.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.common.api.constant.MultipartKey;
import com.benchpress200.photique.support.base.BaseControllerTest;
import com.benchpress200.photique.support.fixture.MultipartFileFixture;
import com.benchpress200.photique.support.fixture.MultipartJsonFixture;
import com.benchpress200.photique.user.api.command.support.fixture.ResisterRequestFixture;
import com.benchpress200.photique.user.api.command.support.fixture.UserDetailsUpdateRequestFixture;
import com.benchpress200.photique.user.api.command.support.fixture.UserPasswordResetRequestFixture;
import com.benchpress200.photique.user.api.command.support.fixture.UserPasswordUpdateRequestFixture;
import com.benchpress200.photique.user.application.command.port.in.ResetUserPasswordUseCase;
import com.benchpress200.photique.user.application.command.port.in.ResisterUseCase;
import com.benchpress200.photique.user.application.command.port.in.UpdateUserDetailsUseCase;
import com.benchpress200.photique.user.application.command.port.in.UpdateUserPasswordUseCase;
import com.benchpress200.photique.user.application.command.port.in.WithdrawUseCase;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

@WebMvcTest(
        controllers = UserCommandController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@DisplayName("유저 커맨드 컨트롤러 테스트")
public class UserCommandControllerTest extends BaseControllerTest {

    @MockitoBean
    private ResisterUseCase resisterUseCase;

    @MockitoBean
    private UpdateUserDetailsUseCase updateUserDetailsUseCase;

    @MockitoBean
    private UpdateUserPasswordUseCase updateUserPasswordUseCase;

    @MockitoBean
    private ResetUserPasswordUseCase resetUserPasswordUseCase;

    @MockitoBean
    private WithdrawUseCase withdrawUseCase;

    @Nested
    @DisplayName("회원가입")
    class RegisterTest {
        @Test
        @DisplayName("요청이 유효하면 201을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            ResisterRequestFixture request = ResisterRequestFixture.builder().build();
            MockMultipartFile userPart = MultipartJsonFixture.builder()
                    .key(MultipartKey.USER)
                    .object(request)
                    .objectMapper(objectMapper)
                    .build();
            doNothing().when(resisterUseCase).resister(any());

            // when
            ResultActions resultActions = requestRegister(userPart, null);

            // then
            resultActions
                    .andExpect(status().isCreated());
        }

        @ParameterizedTest
        @DisplayName("이메일이 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.user.api.command.controller.UserCommandControllerTest#invalidEmails")
        public void whenEmailInvalid(String invalidEmail) throws Exception {
            // given
            ResisterRequestFixture request = ResisterRequestFixture.builder()
                    .email(invalidEmail)
                    .build();
            MockMultipartFile userPart = MultipartJsonFixture.builder()
                    .key(MultipartKey.USER)
                    .object(request)
                    .objectMapper(objectMapper)
                    .build();
            doNothing().when(resisterUseCase).resister(any());

            // when
            ResultActions resultActions = requestRegister(userPart, null);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @DisplayName("비밀번호가 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.user.api.command.controller.UserCommandControllerTest#invalidPasswords")
        public void whenPasswordInvalid(String invalidPassword) throws Exception {
            // given
            ResisterRequestFixture request = ResisterRequestFixture.builder()
                    .password(invalidPassword)
                    .build();
            MockMultipartFile userPart = MultipartJsonFixture.builder()
                    .key(MultipartKey.USER)
                    .object(request)
                    .objectMapper(objectMapper)
                    .build();
            doNothing().when(resisterUseCase).resister(any());

            // when
            ResultActions resultActions = requestRegister(userPart, null);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @DisplayName("닉네임이 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.user.api.command.controller.UserCommandControllerTest#invalidNicknames")
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
            doNothing().when(resisterUseCase).resister(any());

            // when
            ResultActions resultActions = requestRegister(userPart, null);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @DisplayName("프로필 사진이 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.user.api.command.controller.UserCommandControllerTest#invalidProfileImages")
        public void register_whenProfileImageIsInvalid(MockMultipartFile invalidProfileImage) throws Exception {
            // given
            ResisterRequestFixture request = ResisterRequestFixture.builder().build();
            MockMultipartFile userPart = MultipartJsonFixture.builder()
                    .key(MultipartKey.USER)
                    .object(request)
                    .objectMapper(objectMapper)
                    .build();
            doNothing().when(resisterUseCase).resister(any());

            // when
            ResultActions resultActions = requestRegister(userPart, invalidProfileImage);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("유저 정보 수정")
    class UpdateUserDetailsTest {
        @Test
        @DisplayName("요청이 유효하면 204를 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            UserDetailsUpdateRequestFixture request = UserDetailsUpdateRequestFixture.builder().build();
            MockMultipartFile userPart = MultipartJsonFixture.builder()
                    .key(MultipartKey.USER)
                    .object(request)
                    .objectMapper(objectMapper)
                    .build();
            doNothing().when(updateUserDetailsUseCase).updateUserDetails(any());

            // when
            ResultActions resultActions = requestUpdateUserDetails("1", userPart, null);

            // then
            resultActions
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("userId가 숫자가 아니면 400을 반환한다")
        public void whenUserIdInvalid() throws Exception {
            // given
            UserDetailsUpdateRequestFixture request = UserDetailsUpdateRequestFixture.builder().build();
            MockMultipartFile userPart = MultipartJsonFixture.builder()
                    .key(MultipartKey.USER)
                    .object(request)
                    .objectMapper(objectMapper)
                    .build();
            doNothing().when(updateUserDetailsUseCase).updateUserDetails(any());

            // when
            ResultActions resultActions = requestUpdateUserDetails("invalid", userPart, null);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @DisplayName("닉네임이 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.user.api.command.controller.UserCommandControllerTest#invalidNicknamesForUpdate")
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
            doNothing().when(updateUserDetailsUseCase).updateUserDetails(any());

            // when
            ResultActions resultActions = requestUpdateUserDetails("1", userPart, null);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("소개가 유효하지 않으면 400을 반환한다")
        public void whenIntroductionInvalid() throws Exception {
            // given
            UserDetailsUpdateRequestFixture request = UserDetailsUpdateRequestFixture.builder()
                    .introduction("a".repeat(51))
                    .build();
            MockMultipartFile userPart = MultipartJsonFixture.builder()
                    .key(MultipartKey.USER)
                    .object(request)
                    .objectMapper(objectMapper)
                    .build();
            doNothing().when(updateUserDetailsUseCase).updateUserDetails(any());

            // when
            ResultActions resultActions = requestUpdateUserDetails("1", userPart, null);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @DisplayName("프로필 사진이 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.user.api.command.controller.UserCommandControllerTest#invalidProfileImagesForUpdate")
        public void whenProfileImageInvalid(MockMultipartFile invalidProfileImage)
                throws Exception {
            // given
            UserDetailsUpdateRequestFixture request = UserDetailsUpdateRequestFixture.builder().build();
            MockMultipartFile userPart = MultipartJsonFixture.builder()
                    .key(MultipartKey.USER)
                    .object(request)
                    .objectMapper(objectMapper)
                    .build();
            doNothing().when(updateUserDetailsUseCase).updateUserDetails(any());

            // when
            ResultActions resultActions = requestUpdateUserDetails("1", userPart, invalidProfileImage);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("유저 비밀번호 수정")
    class UpdateUserPasswordTest {
        @Test
        @DisplayName("요청이 유효하면 204를 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            UserPasswordUpdateRequestFixture request = UserPasswordUpdateRequestFixture.builder().build();
            doNothing().when(updateUserPasswordUseCase).updateUserPassword(any());

            // when
            ResultActions resultActions = requestUpdateUserPassword("1", request);

            // then
            resultActions
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("userId가 숫자가 아니면 400을 반환한다")
        public void whenUserIdInvalid() throws Exception {
            // given
            UserPasswordUpdateRequestFixture request = UserPasswordUpdateRequestFixture.builder().build();
            doNothing().when(updateUserPasswordUseCase).updateUserPassword(any());

            // when
            ResultActions resultActions = requestUpdateUserPassword("invalid", request);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @DisplayName("비밀번호가 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.user.api.command.controller.UserCommandControllerTest#invalidPasswords")
        public void whenPasswordInvalid(String invalidPassword) throws Exception {
            // given
            UserPasswordUpdateRequestFixture request = UserPasswordUpdateRequestFixture.builder()
                    .password(invalidPassword)
                    .build();
            doNothing().when(updateUserPasswordUseCase).updateUserPassword(any());

            // when
            ResultActions resultActions = requestUpdateUserPassword("1", request);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("유저 비밀번호 초기화")
    class ResetUserPasswordTest {
        @Test
        @DisplayName("요청이 유효하면 204를 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            UserPasswordResetRequestFixture request = UserPasswordResetRequestFixture.builder().build();
            doNothing().when(resetUserPasswordUseCase).resetUserPassword(any());

            // when
            ResultActions resultActions = requestResetUserPassword(request);

            // then
            resultActions
                    .andExpect(status().isNoContent());
        }

        @ParameterizedTest
        @DisplayName("이메일이 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.user.api.command.controller.UserCommandControllerTest#invalidEmails")
        public void whenEmailInvalid(String invalidEmail) throws Exception {
            // given
            UserPasswordResetRequestFixture request = UserPasswordResetRequestFixture.builder()
                    .email(invalidEmail)
                    .build();
            doNothing().when(resetUserPasswordUseCase).resetUserPassword(any());

            // when
            ResultActions resultActions = requestResetUserPassword(request);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @DisplayName("비밀번호가 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.user.api.command.controller.UserCommandControllerTest#invalidPasswords")
        public void whenPasswordInvalid(String invalidPassword) throws Exception {
            // given
            UserPasswordResetRequestFixture request = UserPasswordResetRequestFixture.builder()
                    .password(invalidPassword)
                    .build();
            doNothing().when(resetUserPasswordUseCase).resetUserPassword(any());

            // when
            ResultActions resultActions = requestResetUserPassword(request);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("회원탈퇴")
    class WithdrawTest {
        @Test
        @DisplayName("요청이 유효하면 204를 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            doNothing().when(withdrawUseCase).withdraw(any());

            // when
            ResultActions resultActions = requestWithdraw("1");

            // then
            resultActions
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("userId가 숫자가 아니면 400을 반환한다")
        public void whenUserIdInvalid() throws Exception {
            // given
            doNothing().when(withdrawUseCase).withdraw(any());

            // when
            ResultActions resultActions = requestWithdraw("invalid");

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }
    }

    private static Stream<String> invalidEmails() {
        return Stream.of(
                null,           // @NotNull 위반
                "invalid-email" // 이메일 형식 위반
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

    private static Stream<String> invalidNicknames() {
        return Stream.of(
                null,               // @NotNull 위반
                "nick name",        // 공백 포함
                "a".repeat(12)      // 12자 (최댓값 초과)
        );
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

    private static Stream<String> invalidNicknamesForUpdate() {
        return Stream.of(
                "nick name",        // 공백 포함
                "a".repeat(12)      // 12자 (최댓값 초과)
        );
    }

    private static Stream<MockMultipartFile> invalidProfileImagesForUpdate() {
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

    private ResultActions requestRegister(
            MockMultipartFile userPart,
            MockMultipartFile profileImagePart
    ) throws Exception {
        MockMultipartHttpServletRequestBuilder builder = multipart(ApiPath.USER_ROOT).file(userPart);

        if (profileImagePart != null) {
            builder = builder.file(profileImagePart);
        }

        return mockMvc.perform(builder.contentType(MediaType.MULTIPART_FORM_DATA));
    }

    private ResultActions requestResetUserPassword(
            UserPasswordResetRequestFixture request
    ) throws Exception {
        return mockMvc.perform(
                patch(ApiPath.USER_PASSWORD_RESET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
    }

    private ResultActions requestUpdateUserPassword(
            String userId,
            UserPasswordUpdateRequestFixture request
    ) throws Exception {
        return mockMvc.perform(
                patch(ApiPath.USER_PASSWORD, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
    }

    private ResultActions requestUpdateUserDetails(
            String userId,
            MockMultipartFile userPart,
            MockMultipartFile profileImagePart
    ) throws Exception {
        var builder = multipart(HttpMethod.PATCH, ApiPath.USER_DATA, userId).file(userPart);

        if (profileImagePart != null) {
            builder = builder.file(profileImagePart);
        }

        return mockMvc.perform(builder.contentType(MediaType.MULTIPART_FORM_DATA));
    }

    private ResultActions requestWithdraw(String userId) throws Exception {
        return mockMvc.perform(delete(ApiPath.USER_DATA, userId));
    }
}
