package com.benchpress200.photique.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {
    private static final String TRACE_ID = "traceId";
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();

        String traceId = UUID.randomUUID().toString(); // 로깅 추적을 위한 고유 id 생성
        MDC.put(TRACE_ID, traceId); // ThreadLocal 기반으로 동작하는, 실행중인 스레드의 문맥 정보 저장

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String clientIp = extractClientIp(request);

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error(
                    "traceId={} method={} uri={} clientIp={} exception={}",
                    traceId,
                    method,
                    uri,
                    clientIp,
                    e.getClass().getSimpleName(),
                    e
            );

            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();

            if (status >= HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
                log.error(
                        "traceId={} method={} uri={} status={} durationMs={} clientIp={}",
                        traceId,
                        method,
                        uri,
                        status,
                        duration,
                        clientIp
                );
            } else {
                log.info(
                        "traceId={} method={} uri={} status={} duration={}ms clientIp={}",
                        traceId,
                        method,
                        uri,
                        status,
                        duration,
                        clientIp
                );
            }

            MDC.clear();
        }
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader(X_FORWARDED_FOR);

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}
