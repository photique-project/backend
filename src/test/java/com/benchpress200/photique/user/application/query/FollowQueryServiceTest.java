package com.benchpress200.photique.user.application.query;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.support.base.BaseServiceTest;
import com.benchpress200.photique.user.application.query.model.FolloweeSearchQuery;
import com.benchpress200.photique.user.application.query.port.out.persistence.FollowQueryPort;
import com.benchpress200.photique.user.application.query.result.FolloweeSearchResult;
import com.benchpress200.photique.user.application.query.service.FollowQueryService;
import com.benchpress200.photique.user.application.query.support.fixture.FolloweeSearchQueryFixture;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@DisplayName("팔로우 쿼리 서비스 테스트")
public class FollowQueryServiceTest extends BaseServiceTest {
    @InjectMocks
    private FollowQueryService followQueryService;

    @Mock
    private FollowQueryPort followQueryPort;

    @Mock
    private AuthenticationUserProviderPort authenticationUserProviderPort;

    @Nested
    @DisplayName("팔로이 검색")
    class SearchFolloweeTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenQueryValid() {
            // given
            FolloweeSearchQuery query = FolloweeSearchQueryFixture.builder().build();
            Page<User> followeePage = new PageImpl<>(List.of(), PageRequest.of(0, 30), 0);

            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(followeePage).when(followQueryPort).searchFollowee(any(), any(), any());
            doReturn(Set.of()).when(followQueryPort).findFolloweeIds(any(), any());

            // when
            FolloweeSearchResult result = followQueryService.searchFollowee(query);

            // then
            verify(authenticationUserProviderPort).getCurrentUserId();
            verify(followQueryPort).searchFollowee(query.getUserId(), query.getKeyword(), query.getPageable());
            verify(followQueryPort).findFolloweeIds(any(), any());
            assertNotNull(result);
        }

        @Test
        @DisplayName("팔로이 검색에 실패하면 예외를 던진다")
        public void whenSearchFolloweeFails() {
            // given
            FolloweeSearchQuery query = FolloweeSearchQueryFixture.builder().build();

            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doThrow(new RuntimeException()).when(followQueryPort).searchFollowee(any(), any(), any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> followQueryService.searchFollowee(query)
            );
            verify(followQueryPort, never()).findFolloweeIds(any(), any());
        }

        @Test
        @DisplayName("팔로이 아이디 조회에 실패하면 예외를 던진다")
        public void whenFindFolloweeIdsFails() {
            // given
            FolloweeSearchQuery query = FolloweeSearchQueryFixture.builder().build();
            Page<User> followeePage = new PageImpl<>(List.of(), PageRequest.of(0, 30), 0);

            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(followeePage).when(followQueryPort).searchFollowee(any(), any(), any());
            doThrow(new RuntimeException()).when(followQueryPort).findFolloweeIds(any(), any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> followQueryService.searchFollowee(query)
            );
        }
    }
}
