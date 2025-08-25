package com.benchpress200.photique.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    private static final String API_TITLE = "Photique 서버 API";
    private static final String API_DESCRIPTION = "Photique 서버 API 목록입니다.";
    private static final String API_VERSION = "1.0.0";
    private static final String LOCAL_SERVER_URL = "http://localhost:8080";
    private static final String LOCAL_SERVER_DESCRIPTION = "로컬환경 개발 서버";
    private static final String API_GROUP_V1 = "v1";
    private static final String API_GROUP_PATH_PREFIX_V1 = "/api/v1/**";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(API_TITLE)
                        .description(API_DESCRIPTION)
                        .version(API_VERSION))
                .servers(
                        List.of(
                                new Server()
                                        .url(LOCAL_SERVER_URL)
                                        .description(LOCAL_SERVER_DESCRIPTION)
                        )
                );
    }

    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder()
                .group(API_GROUP_V1)
                .pathsToMatch(API_GROUP_PATH_PREFIX_V1)
                .build();
    }
}
