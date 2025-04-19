package com.benchpress200.photique.singlework.infrastructure;

import com.benchpress200.photique.common.transaction.rollbackcontext.ElasticsearchSingleWorkRollbackContext;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.benchpress200.photique.singlework.exception.SingleWorkException;
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
public class SingleWorkViewCountScheduler {
    private final SingleWorkRepository singleWorkRepository;
    private final SingleWorkSearchRepository singleWorkSearchRepository;

    // 마지막 동기화 시점 (초기값: 애플리케이션 시작 기준)
    private LocalDateTime lastSyncTime = LocalDateTime.now().minusSeconds(30);

    @Scheduled(fixedRate = 30_000) // 30초마다 실행
    public void syncViewCountsToElasticsearch() {
        log.info("[Sync] 단일작품 조회수 동기화 시작: {}", lastSyncTime);

        try {
            List<SingleWork> modifiedSingleWorks = singleWorkRepository.findModifiedSince(lastSyncTime);
            log.info("[Sync] 업데이트된 작품 수: {}", modifiedSingleWorks.size());

            for (SingleWork singleWork : modifiedSingleWorks) {
                try {
                    Long singleWorkId = singleWork.getId();
                    Long viewCount = singleWork.getViewCount();

                    // ES 도큐먼트 찾기
                    SingleWorkSearch singleWorkSearch = singleWorkSearchRepository.findById(singleWorkId).orElseThrow(
                            () -> new SingleWorkException("SingleWork with ID " + singleWorkId + " is not found.",
                                    HttpStatus.NOT_FOUND));

                    singleWorkSearch.updateViewCount(viewCount);
                    ElasticsearchSingleWorkRollbackContext.addDocumentToUpdate(singleWorkSearch);

                } catch (Exception e) {
                    log.error("[Sync] 단일 작품 조회수 동기화 실패", e);
                }
            }

        } catch (Exception e) {
            log.error("[Sync] 전체 ViewCount 동기화 실패", e);
        } finally {
            lastSyncTime = LocalDateTime.now();
        }
    }
}
