package com.benchpress200.photique;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/*
각 테스트 클레스에서 사용될 테스트 컨테이너 설정 클래스입니다.
abstract를 통해 상속해서 쓰는 베이스 클래스임을 명시하고 여러 클래스에서 재사용하기 위해 작성됐습니다.
 */
@Testcontainers
public abstract class AbstractTestContainerConfig {
    @Container
    protected static MySQLContainer<?> mysql =
            new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @Container
    protected static ElasticsearchContainer elasticsearch =
            new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.17.9")
                    .withEnv("discovery.type", "single-node");

    @Container
    protected static GenericContainer<?> redis =
            new GenericContainer<>(DockerImageName.parse("redis:7.4.1-alpine3.20"))
                    .withExposedPorts(6379);

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);

        registry.add("spring.elasticsearch.uris", elasticsearch::getHttpHostAddress);

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }
}
