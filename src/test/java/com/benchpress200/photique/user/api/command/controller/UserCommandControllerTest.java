package com.benchpress200.photique.user.api.command.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.common.api.constant.MultipartKey;
import com.benchpress200.photique.support.fixture.MultipartFileFixture;
import com.benchpress200.photique.support.fixture.MultipartJsonFixture;
import com.benchpress200.photique.support.base.BaseControllerTest;
import com.benchpress200.photique.user.api.command.support.fixture.ResisterRequestFixture;
import com.benchpress200.photique.user.application.command.port.in.ResisterUseCase;
import com.benchpress200.photique.user.application.command.port.in.ResetUserPasswordUseCase;
import com.benchpress200.photique.user.application.command.port.in.UpdateUserDetailsUseCase;
import com.benchpress200.photique.user.application.command.port.in.UpdateUserPasswordUseCase;
import com.benchpress200.photique.user.application.command.port.in.WithdrawUseCase;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

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

    @Test
    @DisplayName("회원가입 요청 시 요청이 유효하면 201을 반환한다")
    public void register_whenRequestIsValid() throws Exception {
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
    @DisplayName("회원가입 요청 시 email이 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidEmails")
    public void register_whenEmailIsInvalid(String invalidEmail) throws Exception {
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
    @DisplayName("회원가입 요청 시 password가 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidPasswords")
    public void register_whenPasswordIsInvalid(String invalidPassword) throws Exception {
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
    @DisplayName("회원가입 요청 시 nickname이 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidNicknames")
    public void register_whenNicknameIsInvalid(String invalidNickname) throws Exception {
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
    @DisplayName("회원가입 요청 시 profileImage가 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidProfileImages")
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

    private ResultActions requestRegister(
            MockMultipartFile userPart,
            MockMultipartFile profileImagePart
    ) throws Exception {
        var builder = multipart(ApiPath.USER_ROOT).file(userPart);

        if (profileImagePart != null) {
            builder = builder.file(profileImagePart);
        }

        return mockMvc.perform(builder.contentType(MediaType.MULTIPART_FORM_DATA));
    }
}
