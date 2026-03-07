package com.benchpress200.photique.support.base;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.enumeration.TokenValidationStatus;
import com.benchpress200.photique.auth.domain.vo.TokenValidationResult;
import com.benchpress200.photique.constant.Profile;
import com.benchpress200.photique.user.domain.enumeration.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles(Profile.TEST)
public abstract class BaseControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationTokenManagerPort authenticationTokenManagerPort;

    @BeforeEach
    public void setUpAuthenticationMock() {
        when(authenticationTokenManagerPort.validateToken(any()))
                .thenReturn(
                        TokenValidationResult.builder()
                                .status(TokenValidationStatus.VALID)
                                .userId(1L)
                                .role(Role.USER.getValue())
                                .build()
                );
    }
}
