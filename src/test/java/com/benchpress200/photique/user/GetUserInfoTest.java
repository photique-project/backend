package com.benchpress200.photique.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.user.infrastructure.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.util.Map;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("유저정보 조회 테스트")
public class GetUserInfoTest {
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
    @DisplayName("유저정보 조회 성공")
    public void testSuccessfulGetUserInfo() throws Exception {
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

        ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson)
                )
                .andExpect(status().isCreated());

        Cookie[] cookies = resultActions.andReturn().getResponse().getCookies();
        Cookie authCookie = null;

        for (Cookie cookie : cookies) {
            if ("Authorization".equals(cookie.getName())) {
                authCookie = cookie;
            }
        }

        resultActions = mockMvc.perform(get("/api/v1/users/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(authCookie)
                )
                .andExpect(status().isOk());

        long userId = objectMapper.readTree(resultActions.andReturn().getResponse().getContentAsString())
                .get("data")
                .get("id")
                .asLong();

        // when and then
        mockMvc.perform(get("/api/v1/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(authCookie)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.nickname").value(nickname))
                .andExpect(jsonPath("$.data.profileImage").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.data.coin").value(0));
    }

    @Test
    @DisplayName("존재하지 않는 유저 조회")
    public void testGetUserInfoNotFound() throws Exception {
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

        ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson)
                )
                .andExpect(status().isCreated());

        Cookie[] cookies = resultActions.andReturn().getResponse().getCookies();
        Cookie authCookie = null;

        for (Cookie cookie : cookies) {
            if ("Authorization".equals(cookie.getName())) {
                authCookie = cookie;
            }
        }

        // when and then
        mockMvc.perform(get("/api/v1/users/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(authCookie)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("인증토큰없이 유저조회")
    public void testGetUserInfoWithoutToken() throws Exception {
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
        mockMvc.perform(get("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized());
    }
}
