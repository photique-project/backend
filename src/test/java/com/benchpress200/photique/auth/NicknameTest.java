package com.benchpress200.photique.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.user.infrastructure.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("닉네임 테스트")
public class NicknameTest {
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
        userRepository.flush();
    }

    @Test
    @DisplayName("닉네임 중복검사 성공")
    public void testSuccessfullyValidationForNickname() throws Exception {
        // given
        String email = "example@naver.com";
        String password = "password12!@";
        String nickname = "test";
        String newNickname = "test1";

        mockMvc.perform(multipart("/api/v1/users")
                        .param(EMAIL_KEY, email)
                        .param(PASSWORD_KEY, password)
                        .param(NICKNAME_KEY, nickname)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated());

        // when and then
        mockMvc.perform(get("/api/v1/auth/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("nickname", newNickname)
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("닉네임 중복검사 실패")
    public void testFailedValidationForNickname() throws Exception {
        // given
        String email = "example@naver.com";
        String password = "password12!@";
        String nickname = "test";

        mockMvc.perform(multipart("/api/v1/users")
                        .param(EMAIL_KEY, email)
                        .param(PASSWORD_KEY, password)
                        .param(NICKNAME_KEY, nickname)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated());

        // when and then
        mockMvc.perform(get("/api/v1/auth/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("nickname", nickname)
                )
                .andExpect(status().isConflict());
    }
}
