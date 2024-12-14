package com.benchpress200.photique.auth.interceptor;

import com.benchpress200.photique.auth.exception.AuthException;
import com.benchpress200.photique.auth.infrastructure.TokenManager;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class OwnResourceInterceptor implements HandlerInterceptor {
    private final TokenManager tokenManager;

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        if (!handlerMethod.getMethod().isAnnotationPresent(OwnResource.class)) {
            return true;
        }

        String accessToken = findAccessToken(request).orElseThrow(() -> new AuthException("", HttpStatus.UNAUTHORIZED));
        Long requesterId = tokenManager.getUserId(accessToken);
        Long ownerId = findOwnerId(request);

        if (!requesterId.equals(ownerId)) {
            throw new AuthException("Access denied", HttpStatus.FORBIDDEN);
        }

        return true;
    }

    private Optional<String> findAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("Authorization")) {
                return Optional.of(cookie.getValue());
            }
        }

        return Optional.empty();
    }

    private Long findOwnerId(HttpServletRequest request) {
        if ("application/json".equals(request.getContentType())) {
            StringBuilder jsonBody = new StringBuilder();

            try (BufferedReader reader = request.getReader()) {
                String line;

                while ((line = reader.readLine()) != null) {
                    jsonBody.append(line);
                }
            } catch (IOException e) {
                throw new AuthException("IOException", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonBody.toString(), JsonObject.class);

            if (jsonObject.has("user_id")) {
                return jsonObject.get("user_id").getAsLong();
            } else if (jsonObject.has("writer_id")) {
                return jsonObject.get("writer_id").getAsLong();
            }
        }

        String userId = request.getParameter("user_id");

        if (userId != null) {
            return Long.parseLong(userId);
        }

        String writerId = request.getParameter("writer_id");

        if (writerId != null) {
            return Long.parseLong(writerId);
        }

        String id = request.getParameter("id");

        if (id != null) {
            return Long.parseLong(id);
        }

        return null;
    }
}
