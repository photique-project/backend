package com.benchpress200.photique.auth.filter.handler;

import com.benchpress200.photique.common.response.ResponseBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailedHandler implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);

        ResponseBody<?> responseBody = new ResponseBody<>(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Authentication failed",
                null,
                LocalDateTime.now()
        );

        String jsonString = objectMapper.writeValueAsString(responseBody);
        response.getWriter().write(jsonString);
    }
}
