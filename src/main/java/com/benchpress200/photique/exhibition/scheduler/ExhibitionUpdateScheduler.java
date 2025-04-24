package com.benchpress200.photique.exhibition.scheduler;

import com.benchpress200.photique.exhibition.domain.ExhibitionCommentDomainService;
import com.benchpress200.photique.exhibition.domain.ExhibitionDomainService;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionTag;
import com.benchpress200.photique.tag.domain.TagDomainService;
import com.benchpress200.photique.tag.domain.entity.Tag;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExhibitionUpdateScheduler {
    private static final int UPDATE_INTERVAL = 60;

    private final ExhibitionDomainService exhibitionDomainService;
    private final ExhibitionCommentDomainService exhibitionCommentDomainService;
    private final TagDomainService tagDomainService;
    private final CacheManager cacheManager;

    // 마지막 동기화 시점 (초기값: 애플리케이션 시작 기준)
    private LocalDateTime lastSyncTime = LocalDateTime.now().minusSeconds(UPDATE_INTERVAL);

    @Transactional
    @Scheduled(fixedRate = UPDATE_INTERVAL * 1000) // 1분마다 실행
    public void syncViewCountsToElasticsearch() {
        log.info("[MySQL-ES] 전시회 동기화 시작: {}", lastSyncTime);

        List<Exhibition> modifiedExhibitions = exhibitionDomainService.findExhibitionsModifiedSince(lastSyncTime);
        log.info("[MySQL-ES] 동기화 전시회 수: {}", modifiedExhibitions.size());

        // 동기화 대상있다면 해당 검색 데이터 캐시 초기화
        if (!modifiedExhibitions.isEmpty()) {
            Cache cache = cacheManager.getCache("searchExhibitionPage");
            if (cache != null) {
                cache.clear();
            }
        }

        List<ExhibitionSearch> exhibitionSearchesToUpdate = new ArrayList<>();

        modifiedExhibitions.forEach(exhibition -> {
            List<ExhibitionTag> exhibitionTags = exhibitionDomainService.findExhibitionTag(exhibition);
            List<Tag> tags = tagDomainService.findExhibitionTags(exhibitionTags);

            List<String> tagNames = tags.stream()
                    .map(Tag::getName)
                    .toList();

            // 여기차례
            // 전시회 오픈해ㅔㅆ을 때 간헐적 이슈 해결
            long likeCount = exhibitionDomainService.countLike(exhibition);
            long commentCount = exhibitionCommentDomainService.countComments(exhibition);

            exhibitionSearchesToUpdate.add(
                    ExhibitionSearch.builder()
                            .id(exhibition.getId())
                            .writerId(exhibition.getWriter().getId())
                            .writerNickname(exhibition.getWriter().getNickname())
                            .writerProfileImage(exhibition.getWriter().getProfileImage())
                            .writerIntroduction(exhibition.getWriter().getIntroduction())
                            .cardColor(exhibition.getCardColor())
                            .title(exhibition.getTitle())
                            .description(exhibition.getDescription())
                            .tags(tagNames)
                            .likeCount(likeCount)
                            .viewCount(exhibition.getViewCount())
                            .commentCount(commentCount)
                            .createdAt(exhibition.getCreatedAt())
                            .build()
            );
        });

        // 벌크 업데이트
        exhibitionDomainService.updateAllExhibitionSearch(exhibitionSearchesToUpdate);

        // 동기화 시간 최신화
        lastSyncTime = LocalDateTime.now();
    }
}
