package com.benchpress200.photique.user.performance;

import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.infrastructure.persistence.jpa.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Disabled("기능 테스트가 아닌 성능 테스트이므로 빌드 전 테스트 단계에서는 제외")
public class SearchPerformanceTest {
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("Like 연산과 FTS 성능 비교 테스트")
    void 유저_검색_성능_비교_테스트() {
        // 매칭접두어가 많으면 풀스캔때림;;
        // 실행계획 확인
        String keyword = "abcd";
        Pageable pageable = PageRequest.of(0, 30);

        // LIKE 측정
        long start = System.nanoTime();
        Page<User> users = userRepository.findByNicknameContaining(keyword, pageable);
        int numberOfLike = users.getNumberOfElements();
        long end = System.nanoTime();
        long like = (end - start);

        // LIKE(접두어) 측정
        start = System.nanoTime();
        users = userRepository.findByNicknameStartingWithAndDeletedAtIsNull(keyword, pageable);
        int numberOfPrefixLike = users.getNumberOfElements();
        end = System.nanoTime();
        long prefixLike = (end - start);

        // FTS 측정
        start = System.nanoTime();
        users = userRepository.searchByNicknameFts(keyword, pageable);
        int numberOfFts = users.getNumberOfElements();
        end = System.nanoTime();
        long fts = (end - start);

        System.out.println("LIKE 실행 시간: " + like / 1_000_000 + " ms" + " [" + numberOfLike + "개 조회]");
        System.out.println("접두어 LIKE 실행 시간: " + prefixLike / 1_000_000 + " ms" + " [" + numberOfPrefixLike + "개 조회]");
        System.out.println("FTS 실행 시간: " + fts / 1_000_000 + " ms" + " [" + numberOfFts + "개 조회]");
    }
}
