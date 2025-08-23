package com.benchpress200.photique.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("회원가입 테스트")
public class JoinTest {
    private static final String PROFILE_IMAGE_KEY = "profileImage";
    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";
    private static final String NICKNAME_KEY = "nickname";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 성공")
    public void testSuccessfulJoin() throws Exception {
        // given
        String email = "example@naver.com";
        String password = "password12!@";
        String nickname = "test";
        String profileImageName = "profileImage.jpg";
        String profileImage = "test image content";

        MockMultipartFile imageFile = new MockMultipartFile(
                PROFILE_IMAGE_KEY,
                profileImageName,
                MediaType.IMAGE_JPEG_VALUE,
                profileImage.getBytes()
        );

        // when and then
        mockMvc.perform(multipart("/api/v1/users")
                        .file(imageFile)
                        .param(EMAIL_KEY, email)
                        .param(PASSWORD_KEY, password)
                        .param(NICKNAME_KEY, nickname)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("이메일 포맷 유효성 검사")
    public void testInvalidEmailFormat() throws Exception {
        // given
        String invalidEmail = "invalid-email";
        String password = "Password12!";
        String nickname = "ValidNickname";

        // when and then
        mockMvc.perform(multipart("/api/v1/users")
                        .param(EMAIL_KEY, invalidEmail)
                        .param(PASSWORD_KEY, password)
                        .param(NICKNAME_KEY, nickname)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비밀번호 포맷 유효성 검사")
    public void testInvalidPasswordFormat() throws Exception {
        // given
        String email = "example@naver.com";
        String invalidPassword = "short1!";
        String nickname = "ValidNickname";

        // when and then
        mockMvc.perform(multipart("/api/v1/users")
                        .param(EMAIL_KEY, email)
                        .param(PASSWORD_KEY, invalidPassword)
                        .param(NICKNAME_KEY, nickname)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("닉네임 포맷 유효성 검사")
    public void testInvalidNicknameFormat() throws Exception {
        // given
        String email = "example@naver.com";
        String password = "Password12!";
        String invalidNickname = "Nickname with space";

        // when and then
        mockMvc.perform(multipart("/api/v1/users")
                        .param(EMAIL_KEY, email)
                        .param(PASSWORD_KEY, password)
                        .param(NICKNAME_KEY, invalidNickname)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("프로필 이미지 유효성 검사")
    public void testMissingProfileImage() throws Exception {
        // given
        String email = "example@naver.com";
        String password = "Password12!";
        String nickname = "ValidNickname";
        String profileImageName = "profileImage.psd";
        String profileImage = "test image content";

        MockMultipartFile imageFile = new MockMultipartFile(
                PROFILE_IMAGE_KEY,
                profileImageName,
                MediaType.IMAGE_JPEG_VALUE,
                profileImage.getBytes()
        );

        // when and then
        mockMvc.perform(multipart("/api/v1/users")
                        .file(imageFile)
                        .param(EMAIL_KEY, email)
                        .param(PASSWORD_KEY, password)
                        .param(NICKNAME_KEY, nickname)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }
}
