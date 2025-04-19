package com.benchpress200.photique.exhibition.scheduler;

import com.benchpress200.photique.common.transaction.rollbackcontext.ElasticsearchExhibitionRollbackContext;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import com.benchpress200.photique.exhibition.infrastructure.ExhibitionRepository;
import com.benchpress200.photique.exhibition.infrastructure.ExhibitionSearchRepository;
import com.benchpress200.photique.singlework.exception.SingleWorkException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExhibitionViewCountScheduler {
    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionSearchRepository exhibitionSearchRepository;

    // 마지막 동기화 시점 (초기값: 애플리케이션 시작 기준)
    private LocalDateTime lastSyncTime = LocalDateTime.now().minusSeconds(30);

    @Transactional
    @Scheduled(fixedRate = 30_000) // 30초마다 실행
    public void syncViewCountsToElasticsearch() {
        log.info("[Sync] 전시회 조회수 동기화 시작: {}", lastSyncTime);

        try {
            List<Exhibition> modifiedSingleWorks = exhibitionRepository.findModifiedSince(lastSyncTime);
            log.info("[Sync] 업데이트된 작품 수: {}", modifiedSingleWorks.size());

            for (Exhibition exhibition : modifiedSingleWorks) {
                try {
                    Long exhibitionId = exhibition.getId();
                    Long viewCount = exhibition.getViewCount();

                    // ES 도큐먼트 찾기
                    ExhibitionSearch exhibitionSearch = exhibitionSearchRepository.findById(exhibitionId).orElseThrow(
                            () -> new SingleWorkException("Exhibition with ID " + exhibitionId + " is not found.",
                                    HttpStatus.NOT_FOUND));

                    exhibitionSearch.updateViewCount(viewCount);
                    ElasticsearchExhibitionRollbackContext.addDocumentToUpdate(exhibitionSearch);

                } catch (Exception e) {
                    log.error("[Sync] 전시회 조회수 동기화 실패", e);
                }
            }

        } catch (Exception e) {
            log.error("[Sync] 전체 ViewCount 동기화 실패", e);
        } finally {
            lastSyncTime = LocalDateTime.now();
        }
    }
}
