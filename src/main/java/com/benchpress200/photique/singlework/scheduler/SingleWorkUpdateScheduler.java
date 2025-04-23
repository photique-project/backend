package com.benchpress200.photique.singlework.scheduler;

import com.benchpress200.photique.singlework.domain.SingleWorkCommentDomainService;
import com.benchpress200.photique.singlework.domain.SingleWorkDomainService;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;
import com.benchpress200.photique.tag.domain.TagDomainService;
import com.benchpress200.photique.tag.domain.entity.Tag;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final SingleWorkCommentDomainService singleWorkCommentDomainService;
    private final TagDomainService tagDomainService;

    // 마지막 동기화 시점 (초기값: 애플리케이션 시작 기준)
    private LocalDateTime lastSyncTime = LocalDateTime.now().minusSeconds(UPDATE_INTERVAL);

    @Transactional
    @Scheduled(fixedRate = UPDATE_INTERVAL * 1000) // 1분마다 실행
    public void syncSingleWorksToElasticsearch() {
        log.info("[MySQL-ES] 단일작품 동기화 시작: {}", lastSyncTime);

        List<SingleWork> modifiedSingleWorks = singleWorkDomainService.findSingleWorksModifiedSince(lastSyncTime);
        log.info("[MySQL-ES] 동기화 단일작품 수: {}", modifiedSingleWorks.size());

        List<SingleWorkSearch> singleWorkSearchesToUpdate = new ArrayList<>();

        modifiedSingleWorks.forEach(singleWork -> {
            // 태그, 카테고리, 좋아요 수, 조회수, 댓글 수 카운팅
            List<SingleWorkTag> singleWorkTags = singleWorkDomainService.findSingleWorkTag(singleWork);
            List<Tag> tags = tagDomainService.findTags(singleWorkTags);

            List<String> tagNames = tags.stream()
                    .map(Tag::getName)
                    .toList();

            long likeCount = singleWorkDomainService.countLike(singleWork);
            long commentCount = singleWorkCommentDomainService.countComments(singleWork);

            singleWorkSearchesToUpdate.add(
                    SingleWorkSearch.builder()
                            .id(singleWork.getId())
                            .image(singleWork.getImage())
                            .writerId(singleWork.getWriter().getId())
                            .writerNickname(singleWork.getWriter().getNickname())
                            .writerProfileImage(singleWork.getWriter().getProfileImage())
                            .title(singleWork.getTitle())
                            .tags(tagNames)
                            .category(singleWork.getCategory().getValue())
                            .likeCount(likeCount)
                            .viewCount(singleWork.getViewCount())
                            .commentCount(commentCount)
                            .createdAt(singleWork.getCreatedAt())
                            .build()
            );
        });

        // 벌크 업데이트
        singleWorkDomainService.updateAllSingleWorkSearch(singleWorkSearchesToUpdate);

        // 동기화 시간 최신화
        lastSyncTime = LocalDateTime.now();
    }
}
