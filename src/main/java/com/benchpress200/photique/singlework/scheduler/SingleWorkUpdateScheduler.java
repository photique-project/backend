package com.benchpress200.photique.singlework.scheduler;

import com.benchpress200.photique.common.transaction.rollbackcontext.ElasticsearchSingleWorkRollbackContext;
import com.benchpress200.photique.singlework.domain.SingleWorkDomainService;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SingleWorkUpdateScheduler {
    private static final int UPDATE_INTERVAL = 60;
    private final SingleWorkDomainService singleWorkDomainService;

    // 마지막 동기화 시점 (초기값: 애플리케이션 시작 기준)
    private LocalDateTime lastSyncTime = LocalDateTime.now().minusSeconds(UPDATE_INTERVAL);

    @Transactional
    @Scheduled(fixedRate = UPDATE_INTERVAL * 1000) // 1분마다 실행
    public void syncViewCountsToElasticsearch() {
        log.info("[MySQL-ES] 단일작품 동기화 시작: {}", lastSyncTime);

        try {
            List<SingleWork> modifiedSingleWorks = singleWorkDomainService.findSingleWorksModifiedSince(lastSyncTime);
            log.info("[MySQL-ES] 동기화 단일작품 수: {}", modifiedSingleWorks.size());

            for (SingleWork singleWork : modifiedSingleWorks) {
                try {
                    Long singleWorkId = singleWork.getId();
                    Long viewCount = singleWork.getViewCount();

                    // ES 도큐먼트 찾기
                    SingleWorkSearch singleWorkSearch = singleWorkDomainService.findSingleWorkSearch(singleWorkId);
                    // 뷰카운트 뿐만 아니라 다른 것도 일괄업뎃? 코드확인필요
                    // 하고나서 유저도, 전시회도 확인
                    // 그리고 검색 쿼리확인
                    singleWorkSearch.updateViewCount(viewCount);
                    ElasticsearchSingleWorkRollbackContext.addDocumentToUpdate(singleWorkSearch);
                } catch (Exception e) {
                    log.error("[MySQL-ES] 단일작품 동기화 실패", e);
                }
            }

        } catch (Exception e) {
            log.error("[MySQL-ES] 단일작품 동기화 실패", e);
        } finally {
            lastSyncTime = LocalDateTime.now();
        }
    }
}
