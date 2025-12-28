package com.benchpress200.photique.auth.infrastructure.security.filter.handler;

import com.benchpress200.photique.common.response.ResponseBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);

        ResponseBody<?> responseBody = new ResponseBody<>(
                HttpServletResponse.SC_FORBIDDEN,
                "Access denied",
                null,
                LocalDateTime.now()
        );

        String jsonString = objectMapper.writeValueAsString(responseBody);
        response.getWriter().write(jsonString);
    }
}
