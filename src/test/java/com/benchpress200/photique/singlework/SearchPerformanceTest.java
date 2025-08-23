package com.benchpress200.photique.singlework;

import com.benchpress200.photique.singlework.domain.dto.SingleWorkSearchRequest;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.repository.SingleWorkRepository;
import com.benchpress200.photique.singlework.domain.repository.SingleWorkSearchRepository;
import com.benchpress200.photique.singlework.domain.repository.SingleWorkTagRepository;
import com.benchpress200.photique.tag.domain.entity.Tag;
import com.benchpress200.photique.tag.domain.repository.TagRepository;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.LongStream;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("검색기능 성능 테스트")
public class SearchPerformanceTest {
    private static final Integer INIT_VALUE = 0;
    private static final Integer TEST_DATA_SIZE = 100_000;
    private static final String TEST_USER_EMAIL = "test@example.com";
    private static final String TEST_USER_PASSWORD = "password";
    private static final String TEST_USER_NICKNAME = "test";
    private static final String TEST_USER_PROFILE_IMAGE = "test-profile.jpg";
    private static final String[] TEST_TITLE_KEYWORDS = Arrays.array("골목길 고양이", "지나가는 사람", "노을빛", "큰 나무", "사과");
    private static final String[] TEST_TAGS = Arrays.array("고양이", "사람", "장미꽃", "등산", "퇴근길");
    private static final String TEST_IMAGE = "test-image.jpg";
    private static final String TEST_CAMERA = "Sony";
    private static final String TEST_DESCRIPTION = "description";
    private static final String SEARCH_TARGET = "work";
    private static final String SEARCH_KEYWORD = "고양이";
    private static final String[] SEARCH_CATEGORIES = Arrays.array("landscape", "street");
    private static final String SEARCH_ORDER = "createdAt";

    private Random random;
    private User testUser;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private SingleWorkRepository singleWorkRepository;

    @Autowired
    private SingleWorkTagRepository singleWorkTagRepository;

    @Autowired
    private SingleWorkSearchRepository singleWorkSearchRepository;

    @BeforeEach
    void setup() {
        cleanUp();

        random = new Random();

        // 테스트 유저 1명 생성
        testUser = createTestUser();

        // 태그 데이터 생성
        createTestTags();

        // 테스트 데이터 생성
        List<SingleWorkSearch> singWorkSearchSet = new ArrayList<>();

        LongStream.range(INIT_VALUE, TEST_DATA_SIZE).forEach(i -> {
            String title = createRandomTitle();
            List<Tag> tags = createRandomTags();
            Category category = createRandomCategory();

            SingleWork singleWork = createSingleWork(category, title, tags); // 여기서 SingleWork와 SingleWorkTag 저장
            SingleWorkSearch singleWorkSearch = createSingleWorkSearch(singleWork, title, tags, category);
            singWorkSearchSet.add(singleWorkSearch);
        });

        // Elasticsearch 데이터 생성
        singleWorkSearchRepository.saveAll(singWorkSearchSet);
    }

    @AfterEach
    void cleanUp() {
        singleWorkTagRepository.deleteAll();
        singleWorkRepository.deleteAll();
        singleWorkSearchRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    @DisplayName("MySQL vs Elasticsearch 조회 성능 비교")
    void searchPerformanceTest() {
        SingleWorkSearchRequest singleWorkSearchRequest = new SingleWorkSearchRequest();
        singleWorkSearchRequest.setTarget(SEARCH_TARGET);

        List<String> keywords = List.of(SEARCH_KEYWORD);
        List<String> categories = List.of(SEARCH_CATEGORIES);

        singleWorkSearchRequest.setKeywords(keywords);
        singleWorkSearchRequest.setCategories(categories);

        Pageable pageable = PageRequest.of(0, 30, Sort.by(Sort.Order.desc(SEARCH_ORDER)));

        long startTime = System.nanoTime();
        singleWorkRepository.searchSingleWorks(
                singleWorkSearchRequest.getTarget(),
                singleWorkSearchRequest.getKeywords(),
                singleWorkSearchRequest.getCategories(),
                pageable
        );
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        System.out.println("MySQL 검색 실행시간: " + (duration / 1_000_000.0) + " ms");

        startTime = System.nanoTime();
        singleWorkSearchRepository.search(
                singleWorkSearchRequest.getTarget(),
                singleWorkSearchRequest.getKeywords(),
                singleWorkSearchRequest.getCategories(),
                pageable
        );
        endTime = System.nanoTime();
        duration = endTime - startTime;
        System.out.println("Elasticsearch 검색 실행시간: " + (duration / 1_000_000.0) + " ms");
    }


    private User createTestUser() {
        return userRepository.save(
                User.builder()
                        .email(TEST_USER_EMAIL)
                        .password(TEST_USER_PASSWORD)
                        .nickname(TEST_USER_NICKNAME)
                        .profileImage(TEST_USER_PROFILE_IMAGE)
                        .build()
        );
    }

    private void createTestTags() {
        List<Tag> tags = new ArrayList<>();

        for (int i = INIT_VALUE; i < TEST_TAGS.length; i++) {
            Tag tag = Tag.builder()
                    .name(TEST_TAGS[i])
                    .build();

            tags.add(tag);
        }

        tagRepository.saveAll(tags);
    }

    private String createRandomTitle() {
        return TEST_TITLE_KEYWORDS[random.nextInt(TEST_TITLE_KEYWORDS.length)];
    }

    private Category createRandomCategory() {
        return Category.values()[random.nextInt(Category.values().length)];
    }

    private List<Tag> createRandomTags() {
        List<String> tags = new ArrayList<>();

        int firstRandomIdx = random.nextInt(TEST_TAGS.length);
        int secondRandomIdx = random.nextInt(TEST_TAGS.length);

        while (firstRandomIdx == secondRandomIdx) {
            secondRandomIdx = random.nextInt(TEST_TAGS.length);
        }

        tags.add(TEST_TAGS[firstRandomIdx]);
        tags.add(TEST_TAGS[secondRandomIdx]);

        return tagRepository.findAllByNameIn(tags);
    }

    private SingleWork createSingleWork(Category category, String title, List<Tag> tags) {

        SingleWork singleWork = singleWorkRepository.save(
                SingleWork.builder()
                        .writer(testUser)
                        .image(TEST_IMAGE)
                        .camera(TEST_CAMERA)
                        .category(category)
                        .date(LocalDate.now())
                        .title(title)
                        .description(TEST_DESCRIPTION)
                        .build()
        );

        for (int i = 0; i < 2; i++) {
            singleWorkTagRepository.save(
                    SingleWorkTag.builder()
                            .singleWork(singleWork)
                            .tag(tags.get(i))
                            .build()
            );
        }

        return singleWork;
    }

    private SingleWorkSearch createSingleWorkSearch(
            SingleWork singleWork,
            String title,
            List<Tag> tags,
            Category category
    ) {
        return SingleWorkSearch.builder()
                .id(singleWork.getId())
                .image(TEST_IMAGE)
                .writerId(testUser.getId())
                .writerNickname(testUser.getNickname())
                .writerProfileImage(testUser.getProfileImage())
                .title(title)
                .tags(
                        tags.stream()
                                .map(Tag::getName)
                                .toList()
                )
                .category(category.getValue())
                .likeCount((long) INIT_VALUE)
                .viewCount((long) INIT_VALUE)
                .commentCount((long) INIT_VALUE)
                .createdAt(singleWork.getCreatedAt())
                .build();
    }
}
