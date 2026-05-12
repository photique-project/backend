package com.benchpress200.photique.integration.singlework;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.vo.AuthenticationTokens;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkCommandPort;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkTagCommandPort;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.support.SingleWorkFixture;
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
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

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
}
