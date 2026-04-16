package com.benchpress200.photique.user.application.query;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionQueryPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkQueryPort;
import com.benchpress200.photique.support.base.BaseServiceTest;
import com.benchpress200.photique.user.application.query.model.UserSearchQuery;
import com.benchpress200.photique.user.application.query.port.out.persistence.FollowQueryPort;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.application.query.result.UserSearchResult;
import com.benchpress200.photique.user.application.query.service.UserQueryService;
import com.benchpress200.photique.user.application.query.support.fixture.UserSearchQueryFixture;
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

@DisplayName("유저 쿼리 서비스 테스트")
public class UserQueryServiceTest extends BaseServiceTest {
    @InjectMocks
    private UserQueryService userQueryService;

    @Mock
    private AuthenticationUserProviderPort authenticationUserProviderPort;

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private SingleWorkQueryPort singleWorkQueryPort;

    @Mock
    private ExhibitionQueryPort exhibitionQueryPort;

    @Mock
    private FollowQueryPort followQueryPort;

    @Nested
    @DisplayName("유저 검색")
    class SearchUserTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenQueryValid() {
            // given
            UserSearchQuery query = UserSearchQueryFixture.builder().build();
            Page<User> userPage = new PageImpl<>(List.of(), PageRequest.of(0, 30), 0);

            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(userPage).when(userQueryPort).findByNicknameStartingWithAndDeletedAtIsNull(any(), any());
            doReturn(Set.of()).when(followQueryPort).findFolloweeIds(any(), any());

            // when
            UserSearchResult result = userQueryService.searchUser(query);

            // then
            verify(authenticationUserProviderPort).getCurrentUserId();
            verify(userQueryPort).findByNicknameStartingWithAndDeletedAtIsNull(query.getKeyword(), query.getPageable());
            verify(followQueryPort).findFolloweeIds(any(), any());
            assertNotNull(result);
        }

        @Test
        @DisplayName("유저 검색에 실패하면 예외를 던진다")
        public void whenSearchFails() {
            // given
            UserSearchQuery query = UserSearchQueryFixture.builder().build();

            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doThrow(new RuntimeException()).when(userQueryPort).findByNicknameStartingWithAndDeletedAtIsNull(any(), any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> userQueryService.searchUser(query)
            );
            verify(followQueryPort, never()).findFolloweeIds(any(), any());
        }

        @Test
        @DisplayName("팔로이 아이디 조회에 실패하면 예외를 던진다")
        public void whenFindFolloweeIdsFails() {
            // given
            UserSearchQuery query = UserSearchQueryFixture.builder().build();
            Page<User> userPage = new PageImpl<>(List.of(), PageRequest.of(0, 30), 0);

            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(userPage).when(userQueryPort).findByNicknameStartingWithAndDeletedAtIsNull(any(), any());
            doThrow(new RuntimeException()).when(followQueryPort).findFolloweeIds(any(), any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> userQueryService.searchUser(query)
            );
        }
    }
}
