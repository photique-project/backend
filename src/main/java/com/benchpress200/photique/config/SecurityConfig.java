package com.benchpress200.photique.config;

import com.benchpress200.photique.auth.domain.port.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.infrastructure.security.filter.JwtFilter;
import com.benchpress200.photique.auth.infrastructure.security.filter.LoginFilter;
import com.benchpress200.photique.common.constant.URL;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationTokenManagerPort authenticationTokenManagerPort;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final ObjectMapper objectMapper;
    private final LogoutHandler logoutHandler;
    private final LogoutSuccessHandler logoutSuccessHandler;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;
    private final JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 로그인 필터생성 및 엔드포인트 커스텀
        LoginFilter loginFilter = new LoginFilter(
                authenticationManager(authenticationConfiguration),
                authenticationTokenManagerPort,
                objectMapper
        );
        loginFilter.setFilterProcessesUrl(URL.BASE_URL + URL.AUTH_DOMAIN + URL.LOGIN);

        http // jwt 인증 방식을 위한 기본 설정
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http // 인증 API 설정
                .authorizeHttpRequests((auth) -> auth

                        .requestMatchers(HttpMethod.POST, URL.BASE_URL + URL.AUTH_DOMAIN + URL.LOGIN).permitAll()
                        .requestMatchers(HttpMethod.POST, URL.BASE_URL + URL.AUTH_DOMAIN + URL.JOIN_MAIL).permitAll()
                        .requestMatchers(HttpMethod.POST, URL.BASE_URL + URL.AUTH_DOMAIN + URL.PASSWORD_MAIL)
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, URL.BASE_URL + URL.AUTH_DOMAIN + URL.VALIDATE_CODE)
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, URL.BASE_URL + URL.AUTH_DOMAIN + URL.REFRESH_TOKEN)
                        .permitAll()

                        .requestMatchers(HttpMethod.POST, URL.BASE_URL + URL.USER_DOMAIN).permitAll()
                        .requestMatchers(HttpMethod.GET, URL.BASE_URL + URL.USER_DOMAIN + URL.VALIDATE_NICKNAME)
                        .permitAll()
                        .requestMatchers(HttpMethod.PATCH, URL.BASE_URL + URL.USER_DOMAIN + URL.PASSWORD).permitAll()

                        .requestMatchers(HttpMethod.GET, URL.BASE_URL + URL.SINGLE_WORK_DOMAIN).permitAll()
                        .requestMatchers(HttpMethod.GET, URL.BASE_URL + URL.SINGLE_WORK_DOMAIN + URL.ALL).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                URL.BASE_URL + URL.SINGLE_WORK_DOMAIN + URL.ALL + URL.COMMENT_DOMAIN).permitAll()

                        .requestMatchers(HttpMethod.GET, URL.BASE_URL + URL.EXHIBITION_DOMAIN).permitAll()
                        .anyRequest().authenticated());

        http // 로그아웃 설정
                .logout((logout) -> logout
                        .logoutUrl(URL.BASE_URL + URL.AUTH_DOMAIN + URL.LOGOUT) // 로그아웃 처리 URL
                        .addLogoutHandler(logoutHandler) // 로그아웃 핸들러 추가
                        .logoutSuccessHandler(logoutSuccessHandler) // 로그아웃 성공 핸들러 추가
                );

        http // 인증-인가 예외 처리 설정
                .exceptionHandling((ex) -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                ); // 403 예외 처리 커스텀

        http // 필터 등록
                .addFilterBefore(jwtFilter, LoginFilter.class) // JWT 인증 필터 추가
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class); // 로그인 필터 추가

        return http.build();
    }
}
