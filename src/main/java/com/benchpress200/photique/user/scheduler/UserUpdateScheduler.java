package com.benchpress200.photique.user.scheduler;

import com.benchpress200.photique.exhibition.domain.ExhibitionDomainService;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import com.benchpress200.photique.singlework.domain.SingleWorkDomainService;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.benchpress200.photique.user.domain.UserDomainService;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.entity.UserSearch;
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
public class UserUpdateScheduler {
    private static final int UPDATE_INTERVAL = 60;
    private final UserDomainService userDomainService;
    private final SingleWorkDomainService singleWorkDomainService;
    private final ExhibitionDomainService exhibitionDomainService;

    // 마지막 동기화 시점 (초기값: 애플리케이션 시작 기준)
    private LocalDateTime lastSyncTime = LocalDateTime.now().minusSeconds(UPDATE_INTERVAL);

    // ES 데이터의 업데이트는 즉시 업데이트가 아닌 스케줄러를 통해 업데이트
    @Transactional
    @Scheduled(fixedRate = UPDATE_INTERVAL * 1000) // 1분마다 실행
    public void syncUserDetailsToElasticsearch() {
        log.info("[MySQL-ES] 유저 데이터 동기화 시작: {}", lastSyncTime);

        List<User> modifiedUsers = userDomainService.findUsersModifiedSince(lastSyncTime);
        log.info("[MySQL-ES] 동기화 유저 수: {}", modifiedUsers.size());

        List<UserSearch> userSearches = new ArrayList<>();
        List<SingleWorkSearch> singleWorkSearches = new ArrayList<>();
        List<ExhibitionSearch> exhibitionSearches = new ArrayList<>();

        modifiedUsers.forEach(user -> {
            userSearches.add(
                    UserSearch.builder()
                            .id(user.getId())
                            .profileImage(user.getProfileImage())
                            .nickname(user.getNickname())
                            .introduction(user.getIntroduction())
                            .createdAt(user.getCreatedAt())
                            .build()
            );

            // 해당 유저의 모든 ES 단일작품 조회 및 업데이트
            long userId = user.getId();
            List<SingleWorkSearch> singleWorkSearchesToUpdate = singleWorkDomainService.findSingleWorkSearchesByWriterId(
                    userId);

            singleWorkSearchesToUpdate.forEach(singleWorkSearch -> singleWorkSearch.updateWriterDetails(user));
            singleWorkSearches.addAll(singleWorkSearchesToUpdate);

            // 해당 유저의 모든 ES 전시회 조회 및 업데이트
            List<ExhibitionSearch> exhibitionSearchesToUpdate = exhibitionDomainService.findExhibitionSearchesByWriterId(
                    userId);

            exhibitionSearchesToUpdate.forEach(exhibitionSearch -> exhibitionSearch.updateWriterDetails(user));
            exhibitionSearches.addAll(exhibitionSearchesToUpdate);

        });

        // 벌크 업데이트
        userDomainService.updateAllUserSearch(userSearches);
        singleWorkDomainService.updateAllSingleWorkSearch(singleWorkSearches);
        exhibitionDomainService.updateAllExhibitionSearch(exhibitionSearches);

        // 동기화 시간 최신화
        lastSyncTime = LocalDateTime.now();
    }

}
