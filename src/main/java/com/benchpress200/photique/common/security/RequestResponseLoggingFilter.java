package com.benchpress200.photique.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
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

        log.info(
                "[Remote Address: {}] [Request URL: {}] [Method: {}] [Response Status: {}] [Duration: {} ms]",
                request.getRemoteAddr(),
                request.getRequestURL(),
                request.getMethod(),
                response.getStatus(),
                duration
        );
    }
}
