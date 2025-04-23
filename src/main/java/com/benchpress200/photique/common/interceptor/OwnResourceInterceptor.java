package com.benchpress200.photique.common.interceptor;

import com.benchpress200.photique.auth.domain.AuthDomainService;
import com.benchpress200.photique.auth.exception.AuthException;
import com.benchpress200.photique.exhibition.domain.ExhibitionDomainService;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.singlework.domain.SingleWorkDomainService;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
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
    private static final String APPLICATION_JSON = "application/json";

    private final AuthDomainService authDomainService;
    private final SingleWorkDomainService singleWorkDomainService;
    private final ExhibitionDomainService exhibitionDomainService;

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) {
        // 핸들러 메서드가 아니면 인가 필요없음
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // @OwnResource 어노테이션이 없다면 인가 필요없음
        if (!handlerMethod.getMethod().isAnnotationPresent(OwnResource.class)) {
            return true;
        }

        String accessToken = authDomainService.findAccessToken(request);
        Long requesterId = authDomainService.getUserIdFromToken(accessToken);
        Long ownerId = findOwnerId(request);

        if (!requesterId.equals(ownerId)) {
            throw new AuthException("Access denied", HttpStatus.FORBIDDEN);
        }

        return true;
    }

    /*
     * 요청 데이터에서 요청자 id 반환
     * */
    private Long findOwnerId(final HttpServletRequest request) {
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        // path variable 에 요청자 id가 있을 경우
        String ownerId = pathVariables.get("userId");
        
        if (ownerId != null) {
            return Long.parseLong(ownerId);
        }

        // application/json 요청 바디에 요청자 id가 있을 경우
        if (APPLICATION_JSON.equals(request.getContentType())) {
            String jsonBody = getRequestBody(request);

            if (jsonBody != null && !jsonBody.isEmpty()) {
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

        // delete 요청일 경우 해당 리소스 조회
        if (request.getMethod().equals("DELETE")) {
            String requestURI = request.getRequestURI();
            String[] pathSegments = requestURI.split("/");
            Long id = Long.parseLong(pathSegments[pathSegments.length - 1]);

            if (requestURI.contains("singleworks")) {
                SingleWork singleWork = singleWorkDomainService.findSingleWork(id);
                return singleWork.getWriter().getId();
            }

            if (requestURI.contains("exhibitions")) {
                Exhibition exhibition = exhibitionDomainService.findExhibition(id);
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
