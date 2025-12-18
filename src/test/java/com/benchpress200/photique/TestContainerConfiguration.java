package com.benchpress200.photique;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainerConfiguration {
    private static final String IMAGE_NAME_MYSQL = "mysql:8.0";
    private static final String IMAGE_NAME_ELASTICSEARCH = "docker.elastic.co/elasticsearch/elasticsearch:7.17.9";
    private static final String IMAGE_NAME_REDIS = "redis:7.4.1-alpine3.20";

    @Bean
    @ServiceConnection
    MySQLContainer<?> mySQLContainer() {
        return new MySQLContainer<>(IMAGE_NAME_MYSQL)
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true);
    }

    @Bean
    @ServiceConnection
    ElasticsearchContainer elasticsearchContainer() {
        return new ElasticsearchContainer(IMAGE_NAME_ELASTICSEARCH)
                .withReuse(true);
    }

    @Bean
    @ServiceConnection(name = "redis")
    GenericContainer<?> redisContainer() {
        return new GenericContainer<>(DockerImageName.parse(IMAGE_NAME_REDIS))
                .withReuse(true);
    }
}
