package com.benchpress200.photique.user.application.query;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionQueryPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkQueryPort;
import com.benchpress200.photique.support.base.BaseServiceTest;
import com.benchpress200.photique.user.application.query.model.NicknameValidateQuery;
import com.benchpress200.photique.user.application.query.model.UserSearchQuery;
import com.benchpress200.photique.user.application.query.port.out.persistence.FollowQueryPort;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.application.query.result.MyDetailsResult;
import com.benchpress200.photique.user.application.query.result.NicknameValidateResult;
import com.benchpress200.photique.user.application.query.support.fixture.NicknameValidateQueryFixture;
import com.benchpress200.photique.user.application.query.result.UserDetailsResult;
import com.benchpress200.photique.user.application.query.result.UserSearchResult;
import com.benchpress200.photique.user.application.query.service.UserQueryService;
import com.benchpress200.photique.user.application.query.support.fixture.UserSearchQueryFixture;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.exception.UserNotFoundException;
import com.benchpress200.photique.user.domain.support.UserFixture;
import java.util.List;
import java.util.Optional;
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

    @Nested
    @DisplayName("내 정보 상세 조회")
    class GetMyDetailsTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenQueryValid() {
            // given
            User user = UserFixture.builder().id(1L).build();

            doReturn(user.getId()).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(0L).when(singleWorkQueryPort).countByWriter(any());
            doReturn(0L).when(exhibitionQueryPort).countByWriter(any());
            doReturn(0L).when(followQueryPort).countByFollowee(any());
            doReturn(0L).when(followQueryPort).countByFollower(any());

            // when
            MyDetailsResult result = userQueryService.getMyDetails();

            // then
            verify(authenticationUserProviderPort).getCurrentUserId();
            verify(userQueryPort).findByIdAndDeletedAtIsNull(user.getId());
            verify(singleWorkQueryPort).countByWriter(user);
            verify(exhibitionQueryPort).countByWriter(user);
            verify(followQueryPort).countByFollowee(user);
            verify(followQueryPort).countByFollower(user);
            assertNotNull(result);
        }

        @Test
        @DisplayName("유저가 존재하지 않으면 UserNotFoundException을 던진다")
        public void whenUserNotFound() {
            // given
            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Optional.empty()).when(userQueryPort).findByIdAndDeletedAtIsNull(any());

            // when & then
            assertThrows(
                    UserNotFoundException.class,
                    () -> userQueryService.getMyDetails()
            );
            verify(singleWorkQueryPort, never()).countByWriter(any());
        }

        @Test
        @DisplayName("단일작품 카운팅에 실패하면 예외를 던진다")
        public void whenCountSingleWorkFails() {
            // given
            User user = UserFixture.builder().id(1L).build();

            doReturn(user.getId()).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doThrow(new RuntimeException()).when(singleWorkQueryPort).countByWriter(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> userQueryService.getMyDetails()
            );
            verify(exhibitionQueryPort, never()).countByWriter(any());
        }

        @Test
        @DisplayName("전시회 카운팅에 실패하면 예외를 던진다")
        public void whenCountExhibitionFails() {
            // given
            User user = UserFixture.builder().id(1L).build();

            doReturn(user.getId()).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(0L).when(singleWorkQueryPort).countByWriter(any());
            doThrow(new RuntimeException()).when(exhibitionQueryPort).countByWriter(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> userQueryService.getMyDetails()
            );
            verify(followQueryPort, never()).countByFollowee(any());
        }

        @Test
        @DisplayName("팔로워 카운팅에 실패하면 예외를 던진다")
        public void whenCountFollowerFails() {
            // given
            User user = UserFixture.builder().id(1L).build();

            doReturn(user.getId()).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(0L).when(singleWorkQueryPort).countByWriter(any());
            doReturn(0L).when(exhibitionQueryPort).countByWriter(any());
            doThrow(new RuntimeException()).when(followQueryPort).countByFollowee(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> userQueryService.getMyDetails()
            );
            verify(followQueryPort, never()).countByFollower(any());
        }

        @Test
        @DisplayName("팔로잉 카운팅에 실패하면 예외를 던진다")
        public void whenCountFollowingFails() {
            // given
            User user = UserFixture.builder().id(1L).build();

            doReturn(user.getId()).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(0L).when(singleWorkQueryPort).countByWriter(any());
            doReturn(0L).when(exhibitionQueryPort).countByWriter(any());
            doReturn(0L).when(followQueryPort).countByFollowee(any());
            doThrow(new RuntimeException()).when(followQueryPort).countByFollower(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> userQueryService.getMyDetails()
            );
        }
    }

    @Nested
    @DisplayName("유저 정보 상세 조회")
    class GetUserDetailsTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenQueryValid() {
            // given
            User user = UserFixture.builder().id(1L).build();

            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(0L).when(singleWorkQueryPort).countByWriter(any());
            doReturn(0L).when(exhibitionQueryPort).countByWriter(any());
            doReturn(0L).when(followQueryPort).countByFollowee(any());
            doReturn(0L).when(followQueryPort).countByFollower(any());
            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(false).when(followQueryPort).existsByFollowerIdAndFolloweeId(any(), any());

            // when
            UserDetailsResult result = userQueryService.getUserDetails(user.getId());

            // then
            verify(userQueryPort).findByIdAndDeletedAtIsNull(user.getId());
            verify(singleWorkQueryPort).countByWriter(user);
            verify(exhibitionQueryPort).countByWriter(user);
            verify(followQueryPort).countByFollowee(user);
            verify(followQueryPort).countByFollower(user);
            verify(authenticationUserProviderPort).getCurrentUserId();
            verify(followQueryPort).existsByFollowerIdAndFolloweeId(any(), any());
            assertNotNull(result);
        }

        @Test
        @DisplayName("유저가 존재하지 않으면 UserNotFoundException을 던진다")
        public void whenUserNotFound() {
            // given
            doReturn(Optional.empty()).when(userQueryPort).findByIdAndDeletedAtIsNull(any());

            // when & then
            assertThrows(
                    UserNotFoundException.class,
                    () -> userQueryService.getUserDetails(1L)
            );
            verify(singleWorkQueryPort, never()).countByWriter(any());
        }

        @Test
        @DisplayName("단일작품 카운팅에 실패하면 예외를 던진다")
        public void whenCountSingleWorkFails() {
            // given
            User user = UserFixture.builder().id(1L).build();

            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doThrow(new RuntimeException()).when(singleWorkQueryPort).countByWriter(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> userQueryService.getUserDetails(user.getId())
            );
            verify(exhibitionQueryPort, never()).countByWriter(any());
        }

        @Test
        @DisplayName("전시회 카운팅에 실패하면 예외를 던진다")
        public void whenCountExhibitionFails() {
            // given
            User user = UserFixture.builder().id(1L).build();

            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(0L).when(singleWorkQueryPort).countByWriter(any());
            doThrow(new RuntimeException()).when(exhibitionQueryPort).countByWriter(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> userQueryService.getUserDetails(user.getId())
            );
            verify(followQueryPort, never()).countByFollowee(any());
        }

        @Test
        @DisplayName("팔로워 카운팅에 실패하면 예외를 던진다")
        public void whenCountFollowerFails() {
            // given
            User user = UserFixture.builder().id(1L).build();

            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(0L).when(singleWorkQueryPort).countByWriter(any());
            doReturn(0L).when(exhibitionQueryPort).countByWriter(any());
            doThrow(new RuntimeException()).when(followQueryPort).countByFollowee(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> userQueryService.getUserDetails(user.getId())
            );
            verify(followQueryPort, never()).countByFollower(any());
        }

        @Test
        @DisplayName("팔로잉 카운팅에 실패하면 예외를 던진다")
        public void whenCountFollowingFails() {
            // given
            User user = UserFixture.builder().id(1L).build();

            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(0L).when(singleWorkQueryPort).countByWriter(any());
            doReturn(0L).when(exhibitionQueryPort).countByWriter(any());
            doReturn(0L).when(followQueryPort).countByFollowee(any());
            doThrow(new RuntimeException()).when(followQueryPort).countByFollower(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> userQueryService.getUserDetails(user.getId())
            );
            verify(authenticationUserProviderPort, never()).getCurrentUserId();
        }

        @Test
        @DisplayName("팔로우 여부 조회에 실패하면 예외를 던진다")
        public void whenCheckFollowingFails() {
            // given
            User user = UserFixture.builder().id(1L).build();

            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(0L).when(singleWorkQueryPort).countByWriter(any());
            doReturn(0L).when(exhibitionQueryPort).countByWriter(any());
            doReturn(0L).when(followQueryPort).countByFollowee(any());
            doReturn(0L).when(followQueryPort).countByFollower(any());
            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doThrow(new RuntimeException()).when(followQueryPort).existsByFollowerIdAndFolloweeId(any(), any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> userQueryService.getUserDetails(user.getId())
            );
        }
    }

    @Nested
    @DisplayName("유저 닉네임 중복 검사")
    class ValidateNicknameTest {
        @Test
        @DisplayName("닉네임이 중복되지 않으면 isDuplicated가 false인 결과를 반환한다")
        public void whenNicknameNotDuplicated() {
            // given
            NicknameValidateQuery query = NicknameValidateQueryFixture.builder().build();

            doReturn(false).when(userQueryPort).existsByNickname(any());

            // when
            NicknameValidateResult result = userQueryService.validateNickname(query);

            // then
            verify(userQueryPort).existsByNickname(query.getNickname());
            assertNotNull(result);
            assertFalse(result.isDuplicated());
        }

        @Test
        @DisplayName("닉네임이 중복되면 isDuplicated가 true인 결과를 반환한다")
        public void whenNicknameDuplicated() {
            // given
            NicknameValidateQuery query = NicknameValidateQueryFixture.builder().build();

            doReturn(true).when(userQueryPort).existsByNickname(any());

            // when
            NicknameValidateResult result = userQueryService.validateNickname(query);

            // then
            verify(userQueryPort).existsByNickname(query.getNickname());
            assertNotNull(result);
            assertTrue(result.isDuplicated());
        }

        @Test
        @DisplayName("닉네임 중복 조회에 실패하면 예외를 던진다")
        public void whenExistsByNicknameFails() {
            // given
            NicknameValidateQuery query = NicknameValidateQueryFixture.builder().build();

            doThrow(new RuntimeException()).when(userQueryPort).existsByNickname(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> userQueryService.validateNickname(query)
            );
        }
    }
}
