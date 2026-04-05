package com.benchpress200.photique.support.base;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class BaseTestContainerTest {
    private final static String IMAGE_MYSQL = "mysql:8.0";
    private final static String IMAGE_REDIS = "redis:7.2.6";
    private final static String IMAGE_ELASTICSEARCH = "docker.elastic.co/elasticsearch/elasticsearch:8.6.0";


    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>(IMAGE_MYSQL)
            .withDatabaseName("photique_test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(IMAGE_REDIS)
            .withExposedPorts(6379)
            .withReuse(true);

    @Container
    static ElasticsearchContainer elasticsearch = new ElasticsearchContainer(IMAGE_ELASTICSEARCH)
            .withEnv("xpack.security.enabled", "false")
            .withEnv("xpack.security.http.ssl.enabled", "false")
            .withEnv("discovery.type", "single-node")
            .withCommand("sh", "-c", "elasticsearch-plugin install analysis-nori && elasticsearch")
            .withReuse(true);


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // MySQL
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);

        // Redis
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));

        // Elasticsearch
        registry.add("spring.elasticsearch.uris", elasticsearch::getHttpHostAddress);
        registry.add("spring.elasticsearch.username", () -> "elastic");
        registry.add("spring.elasticsearch.password", () -> "test");
    }

}
