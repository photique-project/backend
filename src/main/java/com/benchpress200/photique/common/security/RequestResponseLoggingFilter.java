package com.benchpress200.photique.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();

        log.info(
                "[Remote Address: {}] [Request URL: {}] [Method: {}]",
                request.getRemoteAddr(),
                request.getRequestURL(),
                request.getMethod()
        );

        filterChain.doFilter(request, response);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        if (response.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            log.error(
                    "[Remote Address: {}] [Request URL: {}] [Method: {}] [Response Status: {}] [Error Message: {}] [Duration: {} ms]",
                    request.getRemoteAddr(),
                    request.getRequestURL(),
                    request.getMethod(),
                    response.getStatus(),
                    request.getAttribute("message"),
                    duration
            );

            return;
        }

        log.info(
                "[Remote Address: {}] [Request URL: {}] [Method: {}] [Response Status: {}] [Info Message: {}] [Duration: {} ms]",
                request.getRemoteAddr(),
                request.getRequestURL(),
                request.getMethod(),
                response.getStatus(),
                request.getAttribute("message"),
                duration
        );
    }
}
