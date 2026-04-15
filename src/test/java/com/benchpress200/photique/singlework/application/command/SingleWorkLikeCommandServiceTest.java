package com.benchpress200.photique.singlework.application.command;

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
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkCommandPort;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkLikeCommandPort;
import com.benchpress200.photique.singlework.application.command.service.SingleWorkLikeCommandService;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkLikeQueryPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkQueryPort;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkLike;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkAlreadyLikedException;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkNotFoundException;
import com.benchpress200.photique.singlework.domain.support.SingleWorkFixture;
import com.benchpress200.photique.support.base.BaseServiceTest;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.exception.UserNotFoundException;
import com.benchpress200.photique.user.domain.support.UserFixture;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@DisplayName("단일작품 좋아요 커맨드 서비스 테스트")
public class SingleWorkLikeCommandServiceTest extends BaseServiceTest {
    @InjectMocks
    private SingleWorkLikeCommandService singleWorkLikeCommandService;

    @Mock
    private AuthenticationUserProviderPort authenticationUserProvider;

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private SingleWorkQueryPort singleWorkQueryPort;

    @Mock
    private SingleWorkCommandPort singleWorkCommandPort;

    @Mock
    private SingleWorkLikeQueryPort singleWorkLikeQueryPort;

    @Mock
    private SingleWorkLikeCommandPort singleWorkLikeCommandPort;

    @Mock
    private OutboxEventFactory outboxEventFactory;

    @Mock
    private OutboxEventPort outboxEventPort;

    @Nested
    @DisplayName("단일작품 좋아요 추가")
    class AddSingleWorkLikeTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenCommandValid() {
            // given
            User user = UserFixture.builder().id(1L).build();
            SingleWork singleWork = SingleWorkFixture.builder().build();
            OutboxEvent outboxEvent = OutboxEventFixture.builder().build();

            doReturn(user.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(singleWork)).when(singleWorkQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(false).when(singleWorkLikeQueryPort).existsByUserIdAndSingleWorkId(any(), any());
            doReturn(null).when(singleWorkLikeCommandPort).save(any());
            doReturn(outboxEvent).when(outboxEventFactory).singleWorkLiked(any());
            doReturn(outboxEvent).when(outboxEventPort).save(any());

            // when
            singleWorkLikeCommandService.addSingleWorkLike(1L);

            // then
            verify(userQueryPort).findByIdAndDeletedAtIsNull(user.getId());
            verify(singleWorkLikeQueryPort).existsByUserIdAndSingleWorkId(user.getId(), 1L);
            verify(singleWorkLikeCommandPort).save(any());
            verify(singleWorkCommandPort).incrementLikeCount(1L);
            verify(outboxEventFactory).singleWorkLiked(any());
            verify(outboxEventPort).save(outboxEvent);
        }

        @Test
        @DisplayName("유저가 존재하지 않으면 UserNotFoundException을 던진다")
        public void whenUserNotFound() {
            // given
            doReturn(1L).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.empty()).when(userQueryPort).findByIdAndDeletedAtIsNull(any());

            // when & then
            assertThrows(
                    UserNotFoundException.class,
                    () -> singleWorkLikeCommandService.addSingleWorkLike(1L)
            );
            verify(singleWorkLikeCommandPort, never()).save(any());
        }

        @Test
        @DisplayName("단일작품이 존재하지 않으면 SingleWorkNotFoundException을 던진다")
        public void whenSingleWorkNotFound() {
            // given
            User user = UserFixture.builder().id(1L).build();

            doReturn(user.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.empty()).when(singleWorkQueryPort).findByIdAndDeletedAtIsNull(any());

            // when & then
            assertThrows(
                    SingleWorkNotFoundException.class,
                    () -> singleWorkLikeCommandService.addSingleWorkLike(1L)
            );
            verify(singleWorkLikeCommandPort, never()).save(any());
        }

        @Test
        @DisplayName("이미 좋아요한 단일작품이면 SingleWorkAlreadyLikedException을 던진다")
        public void whenAlreadyLiked() {
            // given
            User user = UserFixture.builder().id(1L).build();
            SingleWork singleWork = SingleWorkFixture.builder().build();

            doReturn(user.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(singleWork)).when(singleWorkQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(true).when(singleWorkLikeQueryPort).existsByUserIdAndSingleWorkId(any(), any());

            // when & then
            assertThrows(
                    SingleWorkAlreadyLikedException.class,
                    () -> singleWorkLikeCommandService.addSingleWorkLike(1L)
            );
            verify(singleWorkLikeCommandPort, never()).save(any());
        }

        @Test
        @DisplayName("좋아요 저장에 실패하면 예외를 던진다")
        public void whenSaveFails() {
            // given
            User user = UserFixture.builder().id(1L).build();
            SingleWork singleWork = SingleWorkFixture.builder().build();

            doReturn(user.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(singleWork)).when(singleWorkQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(false).when(singleWorkLikeQueryPort).existsByUserIdAndSingleWorkId(any(), any());
            doThrow(new RuntimeException()).when(singleWorkLikeCommandPort).save(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> singleWorkLikeCommandService.addSingleWorkLike(1L)
            );
            verify(outboxEventPort, never()).save(any());
        }

        @Test
        @DisplayName("좋아요 수 증가에 실패하면 예외를 던진다")
        public void whenIncrementLikeCountFails() {
            // given
            User user = UserFixture.builder().id(1L).build();
            SingleWork singleWork = SingleWorkFixture.builder().build();

            doReturn(user.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(singleWork)).when(singleWorkQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(false).when(singleWorkLikeQueryPort).existsByUserIdAndSingleWorkId(any(), any());
            doReturn(null).when(singleWorkLikeCommandPort).save(any());
            doThrow(new RuntimeException()).when(singleWorkCommandPort).incrementLikeCount(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> singleWorkLikeCommandService.addSingleWorkLike(1L)
            );
            verify(outboxEventPort, never()).save(any());
        }

        @Test
        @DisplayName("아웃박스 이벤트 저장에 실패하면 예외를 던진다")
        public void whenOutboxEventSaveFails() {
            // given
            User user = UserFixture.builder().id(1L).build();
            SingleWork singleWork = SingleWorkFixture.builder().build();

            doReturn(user.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(singleWork)).when(singleWorkQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(false).when(singleWorkLikeQueryPort).existsByUserIdAndSingleWorkId(any(), any());
            doReturn(null).when(singleWorkLikeCommandPort).save(any());
            doReturn(null).when(outboxEventFactory).singleWorkLiked(any());
            doThrow(new RuntimeException()).when(outboxEventPort).save(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> singleWorkLikeCommandService.addSingleWorkLike(1L)
            );
        }
    }

    @Nested
    @DisplayName("단일작품 좋아요 취소")
    class CancelSingleWorkLikeTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenCommandValid() {
            // given
            User user = UserFixture.builder().id(1L).build();
            SingleWork singleWork = SingleWorkFixture.builder().build();
            SingleWorkLike singleWorkLike = SingleWorkLike.of(user, singleWork);
            OutboxEvent outboxEvent = OutboxEventFixture.builder().build();

            doReturn(user.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(singleWork)).when(singleWorkQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(singleWorkLike)).when(singleWorkLikeQueryPort).findByUserAndSingleWork(any(), any());
            doReturn(outboxEvent).when(outboxEventFactory).singleWorkUnliked(any());
            doReturn(outboxEvent).when(outboxEventPort).save(any());

            // when
            singleWorkLikeCommandService.cancelSingleWorkLike(1L);

            // then
            verify(singleWorkLikeCommandPort).delete(singleWorkLike);
            verify(singleWorkCommandPort).decrementLikeCount(1L);
            verify(outboxEventFactory).singleWorkUnliked(any());
            verify(outboxEventPort).save(outboxEvent);
        }

        @Test
        @DisplayName("유저가 존재하지 않으면 UserNotFoundException을 던진다")
        public void whenUserNotFound() {
            // given
            doReturn(1L).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.empty()).when(userQueryPort).findByIdAndDeletedAtIsNull(any());

            // when & then
            assertThrows(
                    UserNotFoundException.class,
                    () -> singleWorkLikeCommandService.cancelSingleWorkLike(1L)
            );
            verify(singleWorkLikeCommandPort, never()).delete(any());
        }

        @Test
        @DisplayName("단일작품이 존재하지 않으면 SingleWorkNotFoundException을 던진다")
        public void whenSingleWorkNotFound() {
            // given
            User user = UserFixture.builder().id(1L).build();

            doReturn(user.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.empty()).when(singleWorkQueryPort).findByIdAndDeletedAtIsNull(any());

            // when & then
            assertThrows(
                    SingleWorkNotFoundException.class,
                    () -> singleWorkLikeCommandService.cancelSingleWorkLike(1L)
            );
            verify(singleWorkLikeCommandPort, never()).delete(any());
        }

        @Test
        @DisplayName("좋아요가 존재하지 않으면 아무 처리도 하지 않는다")
        public void whenLikeNotFound() {
            // given
            User user = UserFixture.builder().id(1L).build();
            SingleWork singleWork = SingleWorkFixture.builder().build();

            doReturn(user.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(singleWork)).when(singleWorkQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.empty()).when(singleWorkLikeQueryPort).findByUserAndSingleWork(any(), any());

            // when
            singleWorkLikeCommandService.cancelSingleWorkLike(1L);

            // then
            verify(singleWorkLikeCommandPort, never()).delete(any());
        }

        @Test
        @DisplayName("좋아요 삭제에 실패하면 예외를 던진다")
        public void whenDeleteFails() {
            // given
            User user = UserFixture.builder().id(1L).build();
            SingleWork singleWork = SingleWorkFixture.builder().build();
            SingleWorkLike singleWorkLike = SingleWorkLike.of(user, singleWork);

            doReturn(user.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(singleWork)).when(singleWorkQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(singleWorkLike)).when(singleWorkLikeQueryPort).findByUserAndSingleWork(any(), any());
            doThrow(new RuntimeException()).when(singleWorkLikeCommandPort).delete(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> singleWorkLikeCommandService.cancelSingleWorkLike(1L)
            );
            verify(outboxEventPort, never()).save(any());
        }

        @Test
        @DisplayName("좋아요 수 감소에 실패하면 예외를 던진다")
        public void whenDecrementLikeCountFails() {
            // given
            User user = UserFixture.builder().id(1L).build();
            SingleWork singleWork = SingleWorkFixture.builder().build();
            SingleWorkLike singleWorkLike = SingleWorkLike.of(user, singleWork);

            doReturn(user.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(singleWork)).when(singleWorkQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(singleWorkLike)).when(singleWorkLikeQueryPort).findByUserAndSingleWork(any(), any());
            doThrow(new RuntimeException()).when(singleWorkCommandPort).decrementLikeCount(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> singleWorkLikeCommandService.cancelSingleWorkLike(1L)
            );
            verify(outboxEventPort, never()).save(any());
        }

        @Test
        @DisplayName("아웃박스 이벤트 저장에 실패하면 예외를 던진다")
        public void whenOutboxEventSaveFails() {
            // given
            User user = UserFixture.builder().id(1L).build();
            SingleWork singleWork = SingleWorkFixture.builder().build();
            SingleWorkLike singleWorkLike = SingleWorkLike.of(user, singleWork);

            doReturn(user.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(singleWork)).when(singleWorkQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(singleWorkLike)).when(singleWorkLikeQueryPort).findByUserAndSingleWork(any(), any());
            doReturn(null).when(outboxEventFactory).singleWorkUnliked(any());
            doThrow(new RuntimeException()).when(outboxEventPort).save(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> singleWorkLikeCommandService.cancelSingleWorkLike(1L)
            );
        }
    }
}
