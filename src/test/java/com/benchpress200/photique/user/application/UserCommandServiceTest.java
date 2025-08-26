package com.benchpress200.photique.user.application;

import com.benchpress200.photique.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@DisplayName("유저 도메인 Command 테스트")
@ActiveProfiles("test")
public class UserCommandServiceTest {
    // MySQL
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

//    // Elasticsearch
//    @Container
//    static ElasticsearchContainer elasticsearch =
//            new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.17.9")
//                    .withEnv("discovery.type", "single-node");
//
//    // Redis
//    @Container
//    static GenericContainer<?> redis =
//            new GenericContainer<>(DockerImageName.parse("redis:5.0.3-alpine"))
//                    .withExposedPorts(6379);


    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        // MySQL
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);

//        // Elasticsearch
//        registry.add("spring.elasticsearch.uris", elasticsearch::getHttpHostAddress);
//
//        // Redis
//        registry.add("spring.data.redis.host", redis::getHost);
//        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }


    @Autowired
    UserRepository userRepository;

    // TODO: 테스트 설계
    // TODO: 단위 테스트 다하고 스웨커 응답 문서 정리
    @Test
    @DisplayName("join 메서드 커밋 테스트")
    void join_메서드_커밋_테스트() {

    }

    @Test
    @DisplayName("join 메서드 롤백 테스트 - ")
    void testJoinWith() {

    }

    @Test
    @DisplayName("MySQL 저장 실패 테스트")
    void testJoin() {

    }
}
