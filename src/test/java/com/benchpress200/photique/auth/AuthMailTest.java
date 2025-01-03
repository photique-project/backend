package com.benchpress200.photique.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.domain.entity.AuthCode;
import com.benchpress200.photique.auth.infrastructure.AuthCodeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
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
@DisplayName("메일인증 테스트")
public class AuthMailTest {
    private static final String EMAIL_KEY = "email";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AuthCodeRepository authCodeRepository;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("메일인증 성공")
    public void testSuccessfulMailAuthentication() throws Exception {
        // given
        String email = "example1@naver.com";
        String type = "JOIN";

        String requestJson = objectMapper.writeValueAsString(Map.of(
                EMAIL_KEY, email,
                "type", type
        ));

        mockMvc.perform(post("/api/v1/auth/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpect(status().isCreated());

        AuthCode authCode = authCodeRepository.findById(email).orElseThrow();

        requestJson = objectMapper.writeValueAsString(Map.of(
                "email", email,
                "code", authCode.getCode()
        ));

        // when and then
        mockMvc.perform(post("/api/v1/auth/code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("메일인증 실패")
    public void testFailedMailAuthentication() throws Exception {
        // given
        String email = "example1@naver.com";
        String type = "JOIN";

        String requestJson = objectMapper.writeValueAsString(Map.of(
                "email", email,
                "type", type
        ));

        mockMvc.perform(post("/api/v1/auth/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpect(status().isCreated());

        AuthCode authCode = authCodeRepository.findById(email).orElseThrow();

        requestJson = objectMapper.writeValueAsString(Map.of(
                "email", email,
                "code", authCode.getCode() + "123"
        ));

        // when and then
        mockMvc.perform(post("/api/v1/auth/code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpect(status().isUnauthorized());
    }
}
