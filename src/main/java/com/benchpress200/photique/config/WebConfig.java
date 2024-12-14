package com.benchpress200.photique.config;

import com.benchpress200.photique.auth.interceptor.AuthInterceptor;
import com.benchpress200.photique.auth.interceptor.OwnResourceInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;
    private final OwnResourceInterceptor ownResourceInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).order(0);
        registry.addInterceptor(ownResourceInterceptor).order(1);
    }
}
