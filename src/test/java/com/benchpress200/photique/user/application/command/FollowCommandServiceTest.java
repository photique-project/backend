package com.benchpress200.photique.user.application.command;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.outbox.application.factory.OutboxEventFactory;
import com.benchpress200.photique.outbox.application.port.out.persistence.OutboxEventPort;
import com.benchpress200.photique.outbox.domain.entity.OutboxEvent;
import com.benchpress200.photique.outbox.domain.support.OutboxEventFixture;
import com.benchpress200.photique.support.base.BaseServiceTest;
import com.benchpress200.photique.user.application.command.port.out.persistence.FollowCommandPort;
import com.benchpress200.photique.user.application.command.service.FollowCommandService;
import com.benchpress200.photique.user.application.query.port.out.persistence.FollowQueryPort;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.exception.DuplicatedFollowException;
import com.benchpress200.photique.user.domain.exception.InvalidFollowRequestException;
import com.benchpress200.photique.user.domain.exception.UserNotFoundException;
import com.benchpress200.photique.user.domain.support.FollowFixture;
import com.benchpress200.photique.user.domain.support.UserFixture;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@DisplayName("팔로우 커맨드 서비스 테스트")
public class FollowCommandServiceTest extends BaseServiceTest {
    @InjectMocks
    private FollowCommandService followCommandService;

    @Mock
    private AuthenticationUserProviderPort authenticationUserProviderPort;

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private FollowCommandPort followCommandPort;

    @Mock
    private FollowQueryPort followQueryPort;

    @Mock
    private OutboxEventFactory outboxEventFactory;

    @Mock
    private OutboxEventPort outboxEventPort;

    @Nested
    @DisplayName("팔로우")
    class FollowTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenCommandValid() {
            // given
            User follower = UserFixture.builder().id(1L).build();
            User followee = UserFixture.builder().id(2L).build();
            OutboxEvent outboxEvent = OutboxEventFixture.builder().build();

            doReturn(follower.getId()).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Optional.empty()).when(followQueryPort).findByFollowerIdAndFolloweeId(any(), any());
            doReturn(Optional.of(follower)).when(userQueryPort).findByIdAndDeletedAtIsNull(follower.getId());
            doReturn(Optional.of(followee)).when(userQueryPort).findByIdAndDeletedAtIsNull(followee.getId());
            doReturn(null).when(followCommandPort).save(any());
            doReturn(outboxEvent).when(outboxEventFactory).follow(any());
            doReturn(outboxEvent).when(outboxEventPort).save(any());

            // when
            followCommandService.follow(followee.getId());

            // then
            verify(followQueryPort).findByFollowerIdAndFolloweeId(follower.getId(), followee.getId());
            verify(followCommandPort).save(any());
            verify(outboxEventFactory).follow(any());
            verify(outboxEventPort).save(outboxEvent);
        }

        @Test
        @DisplayName("본인을 팔로우하면 InvalidFollowRequestException을 던진다")
        public void whenSelfFollow() {
            // given
            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();

            // when & then
            assertThrows(
                    InvalidFollowRequestException.class,
                    () -> followCommandService.follow(1L)
            );
            verify(followCommandPort, never()).save(any());
        }

        @Test
        @DisplayName("이미 팔로우 중이면 DuplicatedFollowException을 던진다")
        public void whenAlreadyFollowing() {
            // given
            User follower = UserFixture.builder().id(1L).build();
            User followee = UserFixture.builder().id(2L).build();
            Follow follow = FollowFixture.builder().follower(follower).followee(followee).build();

            doReturn(follower.getId()).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Optional.of(follow)).when(followQueryPort).findByFollowerIdAndFolloweeId(any(), any());

            // when & then
            assertThrows(
                    DuplicatedFollowException.class,
                    () -> followCommandService.follow(followee.getId())
            );
            verify(followCommandPort, never()).save(any());
        }

        @Test
        @DisplayName("팔로워 유저가 존재하지 않으면 UserNotFoundException을 던진다")
        public void whenFollowerNotFound() {
            // given
            User followee = UserFixture.builder().id(2L).build();

            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Optional.empty()).when(followQueryPort).findByFollowerIdAndFolloweeId(any(), any());
            doReturn(Optional.empty()).when(userQueryPort).findByIdAndDeletedAtIsNull(1L);

            // when & then
            assertThrows(
                    UserNotFoundException.class,
                    () -> followCommandService.follow(followee.getId())
            );
            verify(followCommandPort, never()).save(any());
        }

        @Test
        @DisplayName("팔로잉 유저가 존재하지 않으면 UserNotFoundException을 던진다")
        public void whenFolloweeNotFound() {
            // given
            User follower = UserFixture.builder().id(1L).build();
            User followee = UserFixture.builder().id(2L).build();

            doReturn(follower.getId()).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Optional.empty()).when(followQueryPort).findByFollowerIdAndFolloweeId(any(), any());
            doReturn(Optional.of(follower)).when(userQueryPort).findByIdAndDeletedAtIsNull(follower.getId());
            doReturn(Optional.empty()).when(userQueryPort).findByIdAndDeletedAtIsNull(followee.getId());

            // when & then
            assertThrows(
                    UserNotFoundException.class,
                    () -> followCommandService.follow(followee.getId())
            );
            verify(followCommandPort, never()).save(any());
        }

        @Test
        @DisplayName("팔로우 저장에 실패하면 예외를 던진다")
        public void whenSaveFails() {
            // given
            User follower = UserFixture.builder().id(1L).build();
            User followee = UserFixture.builder().id(2L).build();

            doReturn(follower.getId()).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Optional.empty()).when(followQueryPort).findByFollowerIdAndFolloweeId(any(), any());
            doReturn(Optional.of(follower)).when(userQueryPort).findByIdAndDeletedAtIsNull(follower.getId());
            doReturn(Optional.of(followee)).when(userQueryPort).findByIdAndDeletedAtIsNull(followee.getId());
            doThrow(new RuntimeException()).when(followCommandPort).save(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> followCommandService.follow(followee.getId())
            );
            verify(outboxEventPort, never()).save(any());
        }

        @Test
        @DisplayName("아웃박스 이벤트 저장에 실패하면 예외를 던진다")
        public void whenOutboxEventSaveFails() {
            // given
            User follower = UserFixture.builder().id(1L).build();
            User followee = UserFixture.builder().id(2L).build();

            doReturn(follower.getId()).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Optional.empty()).when(followQueryPort).findByFollowerIdAndFolloweeId(any(), any());
            doReturn(Optional.of(follower)).when(userQueryPort).findByIdAndDeletedAtIsNull(follower.getId());
            doReturn(Optional.of(followee)).when(userQueryPort).findByIdAndDeletedAtIsNull(followee.getId());
            doReturn(null).when(followCommandPort).save(any());
            doReturn(null).when(outboxEventFactory).follow(any());
            doThrow(new RuntimeException()).when(outboxEventPort).save(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> followCommandService.follow(followee.getId())
            );
        }
    }

    @Nested
    @DisplayName("언팔로우")
    class UnfollowTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenCommandValid() {
            // given
            User follower = UserFixture.builder().id(1L).build();
            User followee = UserFixture.builder().id(2L).build();
            Follow follow = FollowFixture.builder().follower(follower).followee(followee).build();

            doReturn(follower.getId()).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Optional.of(follow)).when(followQueryPort).findByFollowerIdAndFolloweeId(any(), any());

            // when
            followCommandService.unfollow(followee.getId());

            // then
            verify(followQueryPort).findByFollowerIdAndFolloweeId(follower.getId(), followee.getId());
            verify(followCommandPort).delete(follow);
        }

        @Test
        @DisplayName("본인을 언팔로우하면 InvalidFollowRequestException을 던진다")
        public void whenSelfUnfollow() {
            // given
            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();

            // when & then
            assertThrows(
                    InvalidFollowRequestException.class,
                    () -> followCommandService.unfollow(1L)
            );
            verify(followCommandPort, never()).delete(any());
        }

        @Test
        @DisplayName("팔로우가 존재하지 않으면 아무 처리도 하지 않는다")
        public void whenFollowNotFound() {
            // given
            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Optional.empty()).when(followQueryPort).findByFollowerIdAndFolloweeId(any(), any());

            // when
            followCommandService.unfollow(2L);

            // then
            verify(followCommandPort, never()).delete(any());
        }

        @Test
        @DisplayName("팔로우 삭제에 실패하면 예외를 던진다")
        public void whenDeleteFails() {
            // given
            User follower = UserFixture.builder().id(1L).build();
            User followee = UserFixture.builder().id(2L).build();
            Follow follow = FollowFixture.builder().follower(follower).followee(followee).build();

            doReturn(follower.getId()).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Optional.of(follow)).when(followQueryPort).findByFollowerIdAndFolloweeId(any(), any());
            doThrow(new RuntimeException()).when(followCommandPort).delete(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> followCommandService.unfollow(followee.getId())
            );
        }
    }
}
