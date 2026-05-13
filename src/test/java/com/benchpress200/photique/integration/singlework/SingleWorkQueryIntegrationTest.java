package com.benchpress200.photique.integration.singlework;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.vo.AuthenticationTokens;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkCommandPort;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkLikeCommandPort;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkTagCommandPort;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.benchpress200.photique.singlework.domain.support.SingleWorkFixture;
import com.benchpress200.photique.singlework.domain.support.SingleWorkSearchFixture;
import com.benchpress200.photique.singlework.infrastructure.persistence.elasticsearch.SingleWorkSearchRepository;
import com.benchpress200.photique.singlework.infrastructure.persistence.jpa.SingleWorkLikeRepository;
import com.benchpress200.photique.support.base.BaseIntegrationTest;
import com.benchpress200.photique.user.application.command.port.out.persistence.UserCommandPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.support.UserFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@DisplayName("단일작품 쿼리 API 통합 테스트")
public class SingleWorkQueryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserCommandPort userCommandPort;

    @Autowired
    private AuthenticationTokenManagerPort authenticationTokenManagerPort;

    @Autowired
    private SingleWorkCommandPort singleWorkCommandPort;

    @Autowired
    private SingleWorkTagCommandPort singleWorkTagCommandPort;

    @Autowired
    private SingleWorkLikeCommandPort singleWorkLikeCommandPort;

    @Autowired
    private SingleWorkLikeRepository singleWorkLikeRepository;

    @Autowired
    private SingleWorkSearchRepository singleWorkSearchRepository;

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
        singleWorkSearchRepository.deleteAll();
        singleWorkLikeRepository.deleteAll();
        singleWorkTagCommandPort.deleteAll();
        singleWorkCommandPort.deleteAll();
        userCommandPort.deleteAll();
    }

    @Nested
    @DisplayName("단일작품 상세 조회")
    class GetSingleWorkDetailsTest {

        @Test
        @DisplayName("요청이 유효하면 단일작품 상세 정보를 반환하고 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            SingleWork savedSingleWork = singleWorkCommandPort.save(
                    SingleWorkFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            // when
            ResultActions resultActions = requestGetSingleWorkDetailsAuthenticated(savedSingleWork.getId());

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(savedSingleWork.getId()))
                    .andExpect(jsonPath("$.data.writer.id").value(savedUser.getId()))
                    .andExpect(jsonPath("$.data.writer.nickname").value(savedUser.getNickname()))
                    .andExpect(jsonPath("$.data.writer.profileImage").value(savedUser.getProfileImage()))
                    .andExpect(jsonPath("$.data.writer.introduction").value(savedUser.getIntroduction()))
                    .andExpect(jsonPath("$.data.title").value(savedSingleWork.getTitle()))
                    .andExpect(jsonPath("$.data.description").value(savedSingleWork.getDescription()))
                    .andExpect(jsonPath("$.data.image").value(savedSingleWork.getImage()))
                    .andExpect(jsonPath("$.data.camera").value(savedSingleWork.getCamera()))
                    .andExpect(jsonPath("$.data.category").value(savedSingleWork.getCategory().getValue()))
                    .andExpect(jsonPath("$.data.tags.length()").value(0))
                    .andExpect(jsonPath("$.data.likeCount").value(savedSingleWork.getLikeCount()))
                    .andExpect(jsonPath("$.data.viewCount").value(savedSingleWork.getViewCount()))
                    .andExpect(jsonPath("$.data.isLiked").value(false))
                    .andExpect(jsonPath("$.data.isFollowing").value(false));
        }

        @Test
        @DisplayName("존재하지 않는 단일작품이면 404를 반환한다")
        public void whenSingleWorkNotFound() throws Exception {
            // given
            Long nonExistentId = 9999L;

            // when
            ResultActions resultActions = requestGetSingleWorkDetailsAuthenticated(nonExistentId);

            // then
            resultActions.andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("단일작품 검색")
    class SearchSingleWorkTest {

        @Test
        @DisplayName("요청이 유효하면 단일작품 목록을 반환하고 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            SingleWork savedSingleWork = singleWorkCommandPort.save(
                    SingleWorkFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            singleWorkSearchRepository.save(
                    SingleWorkSearchFixture.builder()
                            .singleWork(savedSingleWork)
                            .build()
            );
            elasticsearchOperations.indexOps(SingleWorkSearch.class).refresh();

            // when
            ResultActions resultActions = requestSearchSingleWorkAuthenticated("work", "기본", null, null);

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
                    .andExpect(jsonPath("$.data.singleWorks.length()").value(1))
                    .andExpect(jsonPath("$.data.singleWorks[0].id").value(savedSingleWork.getId()))
                    .andExpect(jsonPath("$.data.singleWorks[0].writer.id").value(savedUser.getId()))
                    .andExpect(jsonPath("$.data.singleWorks[0].writer.nickname").value(savedUser.getNickname()))
                    .andExpect(jsonPath("$.data.singleWorks[0].writer.profileImage").value(savedUser.getProfileImage()))
                    .andExpect(jsonPath("$.data.singleWorks[0].image").value(savedSingleWork.getImage()))
                    .andExpect(jsonPath("$.data.singleWorks[0].likeCount").value(savedSingleWork.getLikeCount()))
                    .andExpect(jsonPath("$.data.singleWorks[0].viewCount").value(savedSingleWork.getViewCount()))
                    .andExpect(jsonPath("$.data.singleWorks[0].isLiked").value(false));
        }

        @Test
        @DisplayName("검색 대상이 유효하지 않으면 400을 반환한다")
        public void whenTargetInvalid() throws Exception {
            // when
            ResultActions resultActions = requestSearchSingleWorkAuthenticated("INVALID", "단일", null, null);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("키워드 길이가 2 미만이면 400을 반환한다")
        public void whenKeywordTooShort() throws Exception {
            // when
            ResultActions resultActions = requestSearchSingleWorkAuthenticated("work", "가", null, null);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 번호가 음수이면 400을 반환한다")
        public void whenPageNegative() throws Exception {
            // when
            ResultActions resultActions = requestSearchSingleWorkAuthenticated("work", "단일", -1, null);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 사이즈가 유효 범위를 벗어나면 400을 반환한다")
        public void whenSizeOutOfRange() throws Exception {
            // when
            ResultActions resultActions = requestSearchSingleWorkAuthenticated("work", "단일", null, 0);

            // then
            resultActions.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("내 단일작품 검색")
    class SearchMySingleWorkTest {

        @Test
        @DisplayName("요청이 유효하면 내 단일작품 목록을 반환하고 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            SingleWork savedSingleWork = singleWorkCommandPort.save(
                    SingleWorkFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            // when
            ResultActions resultActions = requestSearchMySingleWorkAuthenticated(null, null, null, null);

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
                    .andExpect(jsonPath("$.data.singleWorks.length()").value(1))
                    .andExpect(jsonPath("$.data.singleWorks[0].id").value(savedSingleWork.getId()))
                    .andExpect(jsonPath("$.data.singleWorks[0].writer.id").value(savedUser.getId()))
                    .andExpect(jsonPath("$.data.singleWorks[0].writer.nickname").value(savedUser.getNickname()))
                    .andExpect(jsonPath("$.data.singleWorks[0].writer.profileImage").value(savedUser.getProfileImage()))
                    .andExpect(jsonPath("$.data.singleWorks[0].image").value(savedSingleWork.getImage()))
                    .andExpect(jsonPath("$.data.singleWorks[0].likeCount").value(savedSingleWork.getLikeCount()))
                    .andExpect(jsonPath("$.data.singleWorks[0].viewCount").value(savedSingleWork.getViewCount()))
                    .andExpect(jsonPath("$.data.singleWorks[0].isLiked").value(false));
        }

        @Test
        @DisplayName("인증 토큰이 없으면 401을 반환한다")
        public void whenNotAuthenticated() throws Exception {
            // when
            ResultActions resultActions = requestSearchMySingleWork(null, null, null, null);

            // then
            resultActions.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("키워드가 제목에 매칭되면 해당 단일작품만 반환한다")
        public void whenKeywordMatchesTitle() throws Exception {
            // given
            singleWorkCommandPort.save(
                    SingleWorkFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            // when
            ResultActions resultActions = requestSearchMySingleWorkAuthenticated("기본", null, null, null);

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.singleWorks.length()").value(1));
        }

        @Test
        @DisplayName("키워드가 제목에 매칭되지 않으면 빈 목록을 반환한다")
        public void whenKeywordNotMatches() throws Exception {
            // given
            singleWorkCommandPort.save(
                    SingleWorkFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            // when
            ResultActions resultActions = requestSearchMySingleWorkAuthenticated("없는키워드", null, null, null);

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.singleWorks.length()").value(0));
        }

        @Test
        @DisplayName("키워드 길이가 2 미만이면 400을 반환한다")
        public void whenKeywordTooShort() throws Exception {
            // given
            String invalidKeyword = "가";

            // when
            ResultActions resultActions = requestSearchMySingleWorkAuthenticated(invalidKeyword, null, null, null);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 번호가 음수이면 400을 반환한다")
        public void whenPageNegative() throws Exception {
            // given
            int invalidPage = -1;

            // when
            ResultActions resultActions = requestSearchMySingleWorkAuthenticated(null, invalidPage, null, null);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 사이즈가 유효 범위를 벗어나면 400을 반환한다")
        public void whenSizeOutOfRange() throws Exception {
            // given
            int invalidSize = 0;

            // when
            ResultActions resultActions = requestSearchMySingleWorkAuthenticated(null, null, invalidSize, null);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("정렬 기준이 유효하지 않으면 400을 반환한다")
        public void whenSortInvalid() throws Exception {
            // given
            String invalidSort = "invalid";

            // when
            ResultActions resultActions = requestSearchMySingleWorkAuthenticated(null, null, null, invalidSort);

            // then
            resultActions.andExpect(status().isBadRequest());
        }
    }

    private ResultActions requestGetSingleWorkDetails(Long singleWorkId) throws Exception {
        return mockMvc.perform(
                get(ApiPath.SINGLEWORK_DATA, singleWorkId)
        );
    }

    private ResultActions requestGetSingleWorkDetailsAuthenticated(Long singleWorkId) throws Exception {
        return mockMvc.perform(
                get(ApiPath.SINGLEWORK_DATA, singleWorkId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
    }

    private ResultActions requestSearchSingleWork(
            String target,
            String keyword,
            Integer page,
            Integer size
    ) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.SINGLEWORK_ROOT)
                .param("target", target)
                .param("keyword", keyword);
        if (page != null) {
            builder = builder.param("page", String.valueOf(page));
        }
        if (size != null) {
            builder = builder.param("size", String.valueOf(size));
        }
        return mockMvc.perform(builder);
    }

    private ResultActions requestSearchSingleWorkAuthenticated(
            String target,
            String keyword,
            Integer page,
            Integer size
    ) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.SINGLEWORK_ROOT)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .param("target", target)
                .param("keyword", keyword);
        if (page != null) {
            builder = builder.param("page", String.valueOf(page));
        }
        if (size != null) {
            builder = builder.param("size", String.valueOf(size));
        }
        return mockMvc.perform(builder);
    }

    private ResultActions requestSearchMySingleWork(
            String keyword,
            Integer page,
            Integer size,
            String sort
    ) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.SINGLEWORK_MY_DATA);
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

    private ResultActions requestSearchMySingleWorkAuthenticated(
            String keyword,
            Integer page,
            Integer size,
            String sort
    ) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.SINGLEWORK_MY_DATA)
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
}
