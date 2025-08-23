package com.benchpress200.photique.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.user.domain.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
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
@DisplayName("로그인 테스트")
public class LoginTest {
    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";
    private static final String NICKNAME_KEY = "nickname";

    private final ObjectMapper objectMapper = new ObjectMapper();

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
    @DisplayName("로그인 성공")
    public void testSuccessfulLogin() throws Exception {
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

        String loginRequestJson = objectMapper.writeValueAsString(Map.of(
                "email", email,
                "password", password
        ));

        // when and then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson)
                )
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("로그인 실패")
    public void testFailedLogin() throws Exception {
        // given
        String email = "example@naver.com";
        String password = "password12!@";
        String invalidPassword = "password12!@#";
        String nickname = "test";

        mockMvc.perform(multipart("/api/v1/users")
                        .param(EMAIL_KEY, email)
                        .param(PASSWORD_KEY, password)
                        .param(NICKNAME_KEY, nickname)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated());

        String loginRequestJson = objectMapper.writeValueAsString(Map.of(
                "email", email,
                "password", invalidPassword
        ));

        // when and then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson)
                )
                .andExpect(status().isUnauthorized());
    }
}
