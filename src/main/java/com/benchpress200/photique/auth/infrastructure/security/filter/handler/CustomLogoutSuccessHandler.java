package com.benchpress200.photique.auth.infrastructure.security.filter.handler;

import com.benchpress200.photique.common.api.response.ResponseBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);

        ResponseBody<?> responseBody = new ResponseBody<>(
                HttpServletResponse.SC_OK,
                "Logout completed successfully",
                null,
                LocalDateTime.now()
        );

        String jsonString = objectMapper.writeValueAsString(responseBody);
        response.getWriter().write(jsonString);
    }
}
