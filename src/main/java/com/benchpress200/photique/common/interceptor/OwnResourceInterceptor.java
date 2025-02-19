package com.benchpress200.photique.common.interceptor;

import com.benchpress200.photique.auth.exception.AuthException;
import com.benchpress200.photique.auth.infrastructure.TokenManager;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.infrastructure.ExhibitionRepository;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.exception.SingleWorkException;
import com.benchpress200.photique.singlework.infrastructure.SingleWorkRepository;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

/*
 * 몇몇 API 에서 본인의 리소스만 생성, 수정을 할 수 있도록 검증하는 인터셉터
 * */
@Component
@RequiredArgsConstructor
public class OwnResourceInterceptor implements HandlerInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String JSON_BODY_HEADER = "application/json";

    private final TokenManager tokenManager;
    private final SingleWorkRepository singleWorkRepository;
    private final ExhibitionRepository exhibitionRepository;

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) {
        // 핸들러 메서드가 아니면 인터셉터 통과
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // @OwnResource 어노테이션이 없다면 인터셉터 통과
        if (!handlerMethod.getMethod().isAnnotationPresent(OwnResource.class)) {
            return true;
        }

        String accessToken = findAccessToken(request).orElseThrow(
                () -> new AuthException("Access token not found", HttpStatus.UNAUTHORIZED));
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
            if (cookie.getName().equals(AUTHORIZATION_HEADER)) {
                return Optional.of(cookie.getValue());
            }
        }

        return Optional.empty();
    }

    /*
     * 요청 데이터에서 요청자 id 반환
     * */
    private Long findOwnerId(HttpServletRequest request) {
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        // path variable 에 요청자 id가 있을 경우
        String ownerId = pathVariables.get("userId");
        if (ownerId != null) {
            return Long.parseLong(ownerId);
        }

        // application/json 요청 바디에 요청자 id가 있을 경우
        if (JSON_BODY_HEADER.equals(request.getContentType())) {
            String jsonBody = getRequestBody(request);

            if (jsonBody != null) {
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(jsonBody, JsonObject.class);

                if (jsonObject.has("writerId")) {
                    return jsonObject.get("writerId").getAsLong();
                }

                if (jsonObject.has("userId")) {
                    return jsonObject.get("userId").getAsLong();
                }
            }
        }

        // multipart/form 요청 바디에 요청자 id가 있을 경우
        ownerId = request.getParameter("writerId");

        if (ownerId != null) {
            return Long.parseLong(ownerId);
        }

        // delete 요청일 경우 인프라 조회해서 해당자원 가져오고 작성자아이디 꺼내줘야함
        if (request.getMethod().equals("DELETE")) {
            String requestURI = request.getRequestURI();
            String[] pathSegments = requestURI.split("/");
            Long id = Long.parseLong(pathSegments[pathSegments.length - 1]);

            if (requestURI.contains("singleworks")) {
                SingleWork singleWork = singleWorkRepository.findById(id).orElseThrow(
                        () -> new SingleWorkException("Singlework with id [] is not found.", HttpStatus.NOT_FOUND)
                );

                return singleWork.getWriter().getId();
            }

            if (requestURI.contains("exhibitions")) {
                Exhibition exhibition = exhibitionRepository.findById(id).orElseThrow(
                        () -> new SingleWorkException("Exhibition with id [] is not found.", HttpStatus.NOT_FOUND)
                );

                return exhibition.getWriter().getId();
            }
        }

        return null;
    }

    private String getRequestBody(ServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read request body", e);
        }
        return stringBuilder.toString().trim();
    }
}
