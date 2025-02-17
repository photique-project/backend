package com.benchpress200.photique.config;

import com.benchpress200.photique.user.domain.entity.UserSearch;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ElasticSearchIndexConfig {
    private final ElasticsearchOperations elasticsearchOperations;

    @PostConstruct
    public void createIndex() {
        IndexOperations indexOps = elasticsearchOperations.indexOps(UserSearch.class);

        if (!indexOps.exists()) {
            // ElasticSearch Index Settings 정의
            Document settings = Document.create();
            settings.put("analysis", Map.of(
                    "tokenizer", Map.of(
                            "my_nori_tokenizer", Map.of(
                                    "type", "nori_tokenizer",
                                    "decompound_mode", "mixed",
                                    "discard_punctuation", "false"
                            )
                    ),
                    "analyzer", Map.of(
                            "my_nori_analyzer", Map.of(
                                    "type", "custom",
                                    "tokenizer", "my_nori_tokenizer",
                                    "filter", new String[]{"lowercase", "stop"},
                                    "char_filter", new String[]{"html_strip"}
                            )
                    )
            ));

            indexOps.create(settings);
            indexOps.putMapping(indexOps.createMapping(UserSearch.class));
        }
    }
}
