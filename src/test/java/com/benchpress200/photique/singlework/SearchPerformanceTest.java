package com.benchpress200.photique.singlework;

import static org.assertj.core.api.Assertions.assertThat;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.infrastructure.SingleWorkRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("검색기능 성능 테스트")
public class SearchPerformanceTest {

    @Autowired
    private SingleWorkRepository singleWorkRepository;

    @Autowired
    private RestHighLevelClient elasticsearchClient;

    @BeforeAll
    static void setupTestData() {
        // Generate 1 million test data and save to MySQL and Elasticsearch
        List<SingleWork> testData = IntStream.range(0, 1_000_000)
                .mapToObj(i -> SingleWork.builder()
                        .title("Title " + i)
                        .description("Description for work " + i)
                        .likeCount((long) (Math.random() * 10000))
                        .build())
                .collect(Collectors.toList());

        // Save to MySQL
        singleWorkRepository.saveAll(testData);

        // Save to Elasticsearch (implement bulk indexing here)
        // elasticsearchClient.bulkIndex(testData);
    }

    @Test
    @DisplayName("MySQL 검색 성능 테스트")
    void testMySqlSearchPerformance() {
        String searchKeyword = "Title 12345";

        Instant start = Instant.now();
        List<SingleWork> results = singleWorkRepository.findByTitleContaining(searchKeyword);
        Instant end = Instant.now();

        Duration duration = Duration.between(start, end);
        System.out.println("MySQL 검색 소요 시간: " + duration.toMillis() + " ms");

        assertThat(results).isNotEmpty();
    }

    @Test
    @DisplayName("Elasticsearch 검색 성능 테스트")
    void testElasticsearchSearchPerformance() {
        String searchKeyword = "Title 12345";

        Instant start = Instant.now();
        // Perform Elasticsearch search here using searchKeyword
        // List<SingleWork> results = elasticsearchClient.searchByTitle(searchKeyword);
        Instant end = Instant.now();

        Duration duration = Duration.between(start, end);
        System.out.println("Elasticsearch 검색 소요 시간: " + duration.toMillis() + " ms");

        // assertThat(results).isNotEmpty();
    }

    @Test
    @DisplayName("검색 성능 비교 테스트")
    void compareSearchPerformance() {
        String searchKeyword = "Title 12345";

        // MySQL search
        Instant mysqlStart = Instant.now();
        List<SingleWork> mysqlResults = singleWorkRepository.findByTitleContaining(searchKeyword);
        Instant mysqlEnd = Instant.now();
        Duration mysqlDuration = Duration.between(mysqlStart, mysqlEnd);

        System.out.println("MySQL 검색 소요 시간: " + mysqlDuration.toMillis() + " ms");

        // Elasticsearch search
        Instant esStart = Instant.now();
        // List<SingleWork> esResults = elasticsearchClient.searchByTitle(searchKeyword);
        Instant esEnd = Instant.now();
        Duration esDuration = Duration.between(esStart, esEnd);

        System.out.println("Elasticsearch 검색 소요 시간: " + esDuration.toMillis() + " ms");

        // Percentage difference
        long mysqlTime = mysqlDuration.toMillis();
        long esTime = esDuration.toMillis();
        double percentageDifference = ((double) (mysqlTime - esTime) / mysqlTime) * 100;

        System.out.printf("Elasticsearch는 MySQL보다 %.2f%% %s.\n",
                Math.abs(percentageDifference),
                percentageDifference < 0 ? "빠릅니다" : "느립니다");
    }
}
