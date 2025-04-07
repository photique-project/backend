package com.benchpress200.photique.config;

import com.benchpress200.photique.common.interceptor.AuthInterceptor;
import com.benchpress200.photique.common.interceptor.OwnResourceInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    private final AuthInterceptor authInterceptor;
    private final OwnResourceInterceptor ownResourceInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).order(0);
        registry.addInterceptor(ownResourceInterceptor).order(1);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PATCH", "DELETE", "OPTIONS")
                .allowedOrigins(allowedOrigins)
                .exposedHeaders("Set-Cookie")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);

    }
}
