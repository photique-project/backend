package com.benchpress200.photique.integration.exhibition;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.vo.AuthenticationTokens;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionBookmarkCommandPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionCommandPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionLikeCommandPort;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionBookmark;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionLike;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import com.benchpress200.photique.exhibition.domain.support.ExhibitionFixture;
import com.benchpress200.photique.exhibition.domain.support.ExhibitionSearchFixture;
import com.benchpress200.photique.exhibition.infrastructure.persistence.elasticsearch.ExhibitionSearchRepository;
import com.benchpress200.photique.support.base.BaseIntegrationTest;
import com.benchpress200.photique.user.application.command.port.out.persistence.UserCommandPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.support.UserFixture;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@DisplayName("전시회 쿼리 API 통합 테스트")
public class ExhibitionQueryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserCommandPort userCommandPort;

    @Autowired
    private AuthenticationTokenManagerPort authenticationTokenManagerPort;

    @Autowired
    private ExhibitionCommandPort exhibitionCommandPort;

    @Autowired
    private ExhibitionLikeCommandPort exhibitionLikeCommandPort;

    @Autowired
    private ExhibitionBookmarkCommandPort exhibitionBookmarkCommandPort;

    @Autowired
    private ExhibitionSearchRepository exhibitionSearchRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    private User savedUser;
    private String accessToken;

    @BeforeEach
    void setUp() {
        User user = UserFixture.builder().build();
        savedUser = userCommandPort.save(user);

        AuthenticationTokens tokens = authenticationTokenManagerPort.issueTokens(
                savedUser.getId(),
                savedUser.getRole().name()
        );
        accessToken = tokens.getAccessToken();
    }

    @AfterEach
    void cleanUp() {
        exhibitionBookmarkCommandPort.deleteAll();
        exhibitionLikeCommandPort.deleteAll();
        exhibitionSearchRepository.deleteAll();
        exhibitionCommandPort.deleteAll();
        userCommandPort.deleteAll();
    }

    @Nested
    @DisplayName("전시회 상세 조회")
    class GetExhibitionDetailsTest {

        @Test
        @DisplayName("요청이 유효하면 전시회 상세 정보를 반환하고 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            Exhibition savedExhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            // when
            ResultActions resultActions = requestGetExhibitionDetailsAuthenticated(savedExhibition.getId());

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(savedExhibition.getId()))
                    .andExpect(jsonPath("$.data.writer.id").value(savedUser.getId()))
                    .andExpect(jsonPath("$.data.writer.nickname").value(savedUser.getNickname()))
                    .andExpect(jsonPath("$.data.writer.profileImage").value(savedUser.getProfileImage()))
                    .andExpect(jsonPath("$.data.writer.introduction").value(savedUser.getIntroduction()))
                    .andExpect(jsonPath("$.data.title").value(savedExhibition.getTitle()))
                    .andExpect(jsonPath("$.data.description").value(savedExhibition.getDescription()))
                    .andExpect(jsonPath("$.data.tags.length()").value(0))
                    .andExpect(jsonPath("$.data.works.length()").value(0))
                    .andExpect(jsonPath("$.data.viewCount").value(savedExhibition.getViewCount()))
                    .andExpect(jsonPath("$.data.likeCount").value(savedExhibition.getLikeCount()))
                    .andExpect(jsonPath("$.data.createdAt").exists())
                    .andExpect(jsonPath("$.data.isFollowing").value(false))
                    .andExpect(jsonPath("$.data.isLiked").value(false))
                    .andExpect(jsonPath("$.data.isBookmarked").value(false));
        }

        @Test
        @DisplayName("인증 토큰이 없으면 401을 반환한다")
        public void whenNotAuthenticated() throws Exception {
            // given
            Exhibition savedExhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            // when
            ResultActions resultActions = requestGetExhibitionDetails(savedExhibition.getId());

            // then
            resultActions.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("존재하지 않는 전시회이면 404를 반환한다")
        public void whenExhibitionNotFound() throws Exception {
            // given
            Long nonExistentId = 9999L;

            // when
            ResultActions resultActions = requestGetExhibitionDetailsAuthenticated(nonExistentId);

            // then
            resultActions.andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("전시회 검색")
    class SearchExhibitionTest {

        @Test
        @DisplayName("요청이 유효하면 전시회 목록을 반환하고 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            Exhibition savedExhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            exhibitionSearchRepository.save(
                    ExhibitionSearchFixture.builder()
                            .id(savedExhibition.getId())
                            .writerId(savedUser.getId())
                            .writerNickname(savedUser.getNickname())
                            .writerProfileImage(savedUser.getProfileImage())
                            .build()
            );

            elasticsearchOperations.indexOps(ExhibitionSearch.class).refresh();

            // when
            ResultActions resultActions = requestSearchExhibitionAuthenticated(null, null, null, null);

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.size").value(30))
                    .andExpect(jsonPath("$.data.totalElements").value(1))
                    .andExpect(jsonPath("$.data.totalPages").value(1))
                    .andExpect(jsonPath("$.data.isFirst").value(true))
                    .andExpect(jsonPath("$.data.isLast").value(true))
                    .andExpect(jsonPath("$.data.hasNext").value(false))
                    .andExpect(jsonPath("$.data.hasPrevious").value(false))
                    .andExpect(jsonPath("$.data.exhibitions.length()").value(1))
                    .andExpect(jsonPath("$.data.exhibitions[0].id").value(savedExhibition.getId()))
                    .andExpect(jsonPath("$.data.exhibitions[0].writer.id").value(savedUser.getId()))
                    .andExpect(jsonPath("$.data.exhibitions[0].writer.nickname").value(savedUser.getNickname()))
                    .andExpect(jsonPath("$.data.exhibitions[0].title").value(savedExhibition.getTitle()))
                    .andExpect(jsonPath("$.data.exhibitions[0].description").value(savedExhibition.getDescription()))
                    .andExpect(jsonPath("$.data.exhibitions[0].cardColor").value(savedExhibition.getCardColor()))
                    .andExpect(jsonPath("$.data.exhibitions[0].likeCount").value(savedExhibition.getLikeCount()))
                    .andExpect(jsonPath("$.data.exhibitions[0].viewCount").value(savedExhibition.getViewCount()))
                    .andExpect(jsonPath("$.data.exhibitions[0].isLiked").value(false))
                    .andExpect(jsonPath("$.data.exhibitions[0].isBookmarked").value(false));
        }

        @Test
        @DisplayName("검색 대상이 유효하지 않으면 400을 반환한다")
        public void whenTargetInvalid() throws Exception {
            // given
            Exhibition savedExhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            exhibitionSearchRepository.save(
                    ExhibitionSearchFixture.builder()
                            .id(savedExhibition.getId())
                            .writerId(savedUser.getId())
                            .writerNickname(savedUser.getNickname())
                            .writerProfileImage(savedUser.getProfileImage())
                            .build()
            );

            elasticsearchOperations.indexOps(ExhibitionSearch.class).refresh();

            // when
            ResultActions resultActions = requestSearchExhibitionAuthenticated("invalid", null, null, null);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @DisplayName("키워드가 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.integration.exhibition.ExhibitionQueryIntegrationTest#invalidKeywords")
        public void whenKeywordInvalid(String invalidKeyword) throws Exception {
            // given
            Exhibition savedExhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            exhibitionSearchRepository.save(
                    ExhibitionSearchFixture.builder()
                            .id(savedExhibition.getId())
                            .writerId(savedUser.getId())
                            .writerNickname(savedUser.getNickname())
                            .writerProfileImage(savedUser.getProfileImage())
                            .build()
            );

            elasticsearchOperations.indexOps(ExhibitionSearch.class).refresh();

            // when
            ResultActions resultActions = requestSearchExhibitionAuthenticated(null, invalidKeyword, null, null);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 번호가 음수이면 400을 반환한다")
        public void whenPageNegative() throws Exception {
            // given
            Exhibition savedExhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            exhibitionSearchRepository.save(
                    ExhibitionSearchFixture.builder()
                            .id(savedExhibition.getId())
                            .writerId(savedUser.getId())
                            .writerNickname(savedUser.getNickname())
                            .writerProfileImage(savedUser.getProfileImage())
                            .build()
            );

            elasticsearchOperations.indexOps(ExhibitionSearch.class).refresh();

            // when
            ResultActions resultActions = requestSearchExhibitionAuthenticated(null, null, -1, null);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @DisplayName("페이지 사이즈가 유효 범위를 벗어나면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.integration.exhibition.ExhibitionQueryIntegrationTest#invalidSizes")
        public void whenSizeOutOfRange(String invalidSize) throws Exception {
            // given
            Exhibition savedExhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            exhibitionSearchRepository.save(
                    ExhibitionSearchFixture.builder()
                            .id(savedExhibition.getId())
                            .writerId(savedUser.getId())
                            .writerNickname(savedUser.getNickname())
                            .writerProfileImage(savedUser.getProfileImage())
                            .build()
            );

            elasticsearchOperations.indexOps(ExhibitionSearch.class).refresh();

            // when
            ResultActions resultActions = requestSearchExhibitionAuthenticated(
                    null,
                    null,
                    null,
                    Integer.parseInt(invalidSize)
            );

            // then
            resultActions.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("내 전시회 검색")
    class SearchMyExhibitionTest {

        @Test
        @DisplayName("요청이 유효하면 내 전시회 목록을 반환하고 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            Exhibition savedExhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            // when
            ResultActions resultActions = requestSearchMyExhibitionAuthenticated(null, null, null, null);

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.size").value(30))
                    .andExpect(jsonPath("$.data.totalElements").value(1))
                    .andExpect(jsonPath("$.data.totalPages").value(1))
                    .andExpect(jsonPath("$.data.isFirst").value(true))
                    .andExpect(jsonPath("$.data.isLast").value(true))
                    .andExpect(jsonPath("$.data.hasNext").value(false))
                    .andExpect(jsonPath("$.data.hasPrevious").value(false))
                    .andExpect(jsonPath("$.data.exhibitions.length()").value(1))
                    .andExpect(jsonPath("$.data.exhibitions[0].id").value(savedExhibition.getId()))
                    .andExpect(jsonPath("$.data.exhibitions[0].writer.id").value(savedUser.getId()))
                    .andExpect(jsonPath("$.data.exhibitions[0].writer.nickname").value(savedUser.getNickname()))
                    .andExpect(jsonPath("$.data.exhibitions[0].writer.profileImage").value(savedUser.getProfileImage()))
                    .andExpect(jsonPath("$.data.exhibitions[0].title").value(savedExhibition.getTitle()))
                    .andExpect(jsonPath("$.data.exhibitions[0].description").value(savedExhibition.getDescription()))
                    .andExpect(jsonPath("$.data.exhibitions[0].cardColor").value(savedExhibition.getCardColor()))
                    .andExpect(jsonPath("$.data.exhibitions[0].likeCount").value(savedExhibition.getLikeCount()))
                    .andExpect(jsonPath("$.data.exhibitions[0].viewCount").value(savedExhibition.getViewCount()))
                    .andExpect(jsonPath("$.data.exhibitions[0].isLiked").value(false))
                    .andExpect(jsonPath("$.data.exhibitions[0].isBookmarked").value(false));
        }

        @Test
        @DisplayName("인증 토큰이 없으면 401을 반환한다")
        public void whenNotAuthenticated() throws Exception {
            // given
            exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            // when
            ResultActions resultActions = requestSearchMyExhibition(null, null, null, null);

            // then
            resultActions.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("좋아요한 전시회이면 isLiked가 true인 목록을 반환하고 200을 반환한다")
        public void whenAuthenticatedAndExhibitionLiked() throws Exception {
            // given
            Exhibition savedExhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            exhibitionLikeCommandPort.save(ExhibitionLike.of(savedUser, savedExhibition));

            // when
            ResultActions resultActions = requestSearchMyExhibitionAuthenticated(null, null, null, null);

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.exhibitions[0].isLiked").value(true));
        }

        @Test
        @DisplayName("북마크한 전시회이면 isBookmarked가 true인 목록을 반환하고 200을 반환한다")
        public void whenAuthenticatedAndExhibitionBookmarked() throws Exception {
            // given
            Exhibition savedExhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            exhibitionBookmarkCommandPort.save(ExhibitionBookmark.of(savedUser, savedExhibition));

            // when
            ResultActions resultActions = requestSearchMyExhibitionAuthenticated(null, null, null, null);

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.exhibitions[0].isBookmarked").value(true));
        }

        @Test
        @DisplayName("키워드가 제목에 포함되면 해당 전시회를 반환하고 200을 반환한다")
        public void whenKeywordMatchesTitle() throws Exception {
            // given
            Exhibition savedExhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            String keyword = savedExhibition.getTitle();

            // when
            ResultActions resultActions = requestSearchMyExhibitionAuthenticated(keyword, null, null, null);

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.exhibitions.length()").value(1));
        }

        @Test
        @DisplayName("키워드가 제목에 포함되지 않으면 빈 목록을 반환하고 200을 반환한다")
        public void whenKeywordNotMatchesTitle() throws Exception {
            // given
            exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            String keyword = "없는키워드";

            // when
            ResultActions resultActions = requestSearchMyExhibitionAuthenticated(keyword, null, null, null);

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.exhibitions.length()").value(0));
        }

        @ParameterizedTest
        @DisplayName("키워드가 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.integration.exhibition.ExhibitionQueryIntegrationTest#invalidKeywords")
        public void whenKeywordInvalid(String invalidKeyword) throws Exception {
            // when
            ResultActions resultActions = requestSearchMyExhibitionAuthenticated(invalidKeyword, null, null, null);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 번호가 음수이면 400을 반환한다")
        public void whenPageNegative() throws Exception {
            // given
            int negativePage = -1;

            // when
            ResultActions resultActions = requestSearchMyExhibitionAuthenticated(null, negativePage, null, null);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @DisplayName("페이지 사이즈가 유효 범위를 벗어나면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.integration.exhibition.ExhibitionQueryIntegrationTest#invalidSizes")
        public void whenSizeOutOfRange(String invalidSize) throws Exception {
            // when
            ResultActions resultActions = requestSearchMyExhibitionAuthenticated(
                    null,
                    null,
                    Integer.parseInt(invalidSize),
                    null
            );

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("정렬 기준이 유효하지 않으면 400을 반환한다")
        public void whenSortInvalid() throws Exception {
            // given
            String invalidSort = "invalid";

            // when
            ResultActions resultActions = requestSearchMyExhibitionAuthenticated(null, null, null, invalidSort);

            // then
            resultActions.andExpect(status().isBadRequest());
        }
    }

    private static Stream<String> invalidKeywords() {
        return Stream.of(
                "가",               // 최솟값 미만 (1자)
                "a".repeat(101)     // 최댓값 초과 (101자)
        );
    }

    private static Stream<String> invalidSizes() {
        return Stream.of(
                "0",    // 최솟값 미만
                "51"    // 최댓값 초과
        );
    }

    private ResultActions requestGetExhibitionDetails(Long exhibitionId) throws Exception {
        return mockMvc.perform(
                get(ApiPath.EXHIBITION_DATA, exhibitionId)
        );
    }

    private ResultActions requestGetExhibitionDetailsAuthenticated(Long exhibitionId) throws Exception {
        return mockMvc.perform(
                get(ApiPath.EXHIBITION_DATA, exhibitionId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
    }

    private ResultActions requestSearchMyExhibition(
            String keyword,
            Integer page,
            Integer size,
            String sort
    ) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.EXHIBITION_MY_DATA);
        if (keyword != null) {
            builder = builder.param("keyword", keyword);
        }
        if (page != null) {
            builder = builder.param("page", String.valueOf(page));
        }
        if (size != null) {
            builder = builder.param("size", String.valueOf(size));
        }
        if (sort != null) {
            builder = builder.param("sort", sort);
        }
        return mockMvc.perform(builder);
    }

    private ResultActions requestSearchMyExhibitionAuthenticated(
            String keyword,
            Integer page,
            Integer size,
            String sort
    ) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.EXHIBITION_MY_DATA)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        if (keyword != null) {
            builder = builder.param("keyword", keyword);
        }
        if (page != null) {
            builder = builder.param("page", String.valueOf(page));
        }
        if (size != null) {
            builder = builder.param("size", String.valueOf(size));
        }
        if (sort != null) {
            builder = builder.param("sort", sort);
        }
        return mockMvc.perform(builder);
    }

    private ResultActions requestSearchExhibition(
            String target,
            String keyword,
            Integer page,
            Integer size
    ) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.EXHIBITION_ROOT);
        if (target != null) {
            builder = builder.param("target", target);
        }
        if (keyword != null) {
            builder = builder.param("keyword", keyword);
        }
        if (page != null) {
            builder = builder.param("page", String.valueOf(page));
        }
        if (size != null) {
            builder = builder.param("size", String.valueOf(size));
        }
        return mockMvc.perform(builder);
    }

    private ResultActions requestSearchExhibitionAuthenticated(
            String target,
            String keyword,
            Integer page,
            Integer size
    ) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.EXHIBITION_ROOT)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        if (target != null) {
            builder = builder.param("target", target);
        }
        if (keyword != null) {
            builder = builder.param("keyword", keyword);
        }
        if (page != null) {
            builder = builder.param("page", String.valueOf(page));
        }
        if (size != null) {
            builder = builder.param("size", String.valueOf(size));
        }
        return mockMvc.perform(builder);
    }
}
