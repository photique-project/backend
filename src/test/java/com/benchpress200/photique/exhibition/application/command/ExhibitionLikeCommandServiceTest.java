package com.benchpress200.photique.exhibition.application.command;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionCommandPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionLikeCommandPort;
import com.benchpress200.photique.exhibition.application.command.service.ExhibitionLikeCommandService;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionLikeQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionTagQueryPort;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionAlreadyLikedException;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionNotFoundException;
import com.benchpress200.photique.exhibition.domain.support.ExhibitionFixture;
import com.benchpress200.photique.outbox.application.factory.OutboxEventFactory;
import com.benchpress200.photique.outbox.application.port.out.persistence.OutboxEventPort;
import com.benchpress200.photique.outbox.domain.entity.OutboxEvent;
import com.benchpress200.photique.outbox.domain.support.OutboxEventFixture;
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

@DisplayName("전시회 좋아요 커맨드 서비스 테스트")
public class ExhibitionLikeCommandServiceTest extends BaseServiceTest {
    @InjectMocks
    private ExhibitionLikeCommandService exhibitionLikeCommandService;

    @Mock
    private AuthenticationUserProviderPort authenticationUserProvider;

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private ExhibitionQueryPort exhibitionQueryPort;

    @Mock
    private ExhibitionCommandPort exhibitionCommandPort;

    @Mock
    private ExhibitionLikeQueryPort exhibitionLikeQueryPort;

    @Mock
    private ExhibitionLikeCommandPort exhibitionLikeCommandPort;

    @Mock
    private ExhibitionTagQueryPort exhibitionTagQueryPort;

    @Mock
    private OutboxEventFactory outboxEventFactory;

    @Mock
    private OutboxEventPort outboxEventPort;

    @Nested
    @DisplayName("전시회 좋아요 추가")
    class AddExhibitionLikeTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenCommandValid() {
            // given
            User user = UserFixture.builder().id(1L).build();
            Exhibition exhibition = ExhibitionFixture.builder().id(1L).build();
            OutboxEvent outboxEvent = OutboxEventFixture.builder().build();

            doReturn(user.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(exhibition)).when(exhibitionQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(false).when(exhibitionLikeQueryPort).existsByUserIdAndExhibitionId(any(), any());
            doReturn(null).when(exhibitionLikeCommandPort).save(any());
            doReturn(outboxEvent).when(outboxEventFactory).exhibitionLiked(any());
            doReturn(outboxEvent).when(outboxEventPort).save(any());

            // when
            exhibitionLikeCommandService.addExhibitionLike(exhibition.getId());

            // then
            verify(userQueryPort).findByIdAndDeletedAtIsNull(user.getId());
            verify(exhibitionLikeQueryPort).existsByUserIdAndExhibitionId(user.getId(), exhibition.getId());
            verify(exhibitionLikeCommandPort).save(any());
            verify(exhibitionCommandPort).incrementLikeCount(exhibition.getId());
            verify(outboxEventFactory).exhibitionLiked(any());
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
                    () -> exhibitionLikeCommandService.addExhibitionLike(1L)
            );
            verify(exhibitionLikeCommandPort, never()).save(any());
        }

        @Test
        @DisplayName("전시회가 존재하지 않으면 ExhibitionNotFoundException을 던진다")
        public void whenExhibitionNotFound() {
            // given
            User user = UserFixture.builder().id(1L).build();

            doReturn(user.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.empty()).when(exhibitionQueryPort).findByIdAndDeletedAtIsNull(any());

            // when & then
            assertThrows(
                    ExhibitionNotFoundException.class,
                    () -> exhibitionLikeCommandService.addExhibitionLike(1L)
            );
            verify(exhibitionLikeCommandPort, never()).save(any());
        }

        @Test
        @DisplayName("이미 좋아요한 전시회이면 ExhibitionAlreadyLikedException을 던진다")
        public void whenAlreadyLiked() {
            // given
            User user = UserFixture.builder().id(1L).build();
            Exhibition exhibition = ExhibitionFixture.builder().id(1L).build();

            doReturn(user.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(exhibition)).when(exhibitionQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(true).when(exhibitionLikeQueryPort).existsByUserIdAndExhibitionId(any(), any());

            // when & then
            assertThrows(
                    ExhibitionAlreadyLikedException.class,
                    () -> exhibitionLikeCommandService.addExhibitionLike(exhibition.getId())
            );
            verify(exhibitionLikeCommandPort, never()).save(any());
        }

        @Test
        @DisplayName("좋아요 저장에 실패하면 예외를 던진다")
        public void whenLikeSaveFails() {
            // given
            User user = UserFixture.builder().id(1L).build();
            Exhibition exhibition = ExhibitionFixture.builder().id(1L).build();

            doReturn(user.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(exhibition)).when(exhibitionQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(false).when(exhibitionLikeQueryPort).existsByUserIdAndExhibitionId(any(), any());
            doThrow(new RuntimeException()).when(exhibitionLikeCommandPort).save(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> exhibitionLikeCommandService.addExhibitionLike(exhibition.getId())
            );
            verify(outboxEventPort, never()).save(any());
        }

        @Test
        @DisplayName("좋아요 수 증가에 실패하면 예외를 던진다")
        public void whenIncrementLikeCountFails() {
            // given
            User user = UserFixture.builder().id(1L).build();
            Exhibition exhibition = ExhibitionFixture.builder().id(1L).build();

            doReturn(user.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(exhibition)).when(exhibitionQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(false).when(exhibitionLikeQueryPort).existsByUserIdAndExhibitionId(any(), any());
            doReturn(null).when(exhibitionLikeCommandPort).save(any());
            doThrow(new RuntimeException()).when(exhibitionCommandPort).incrementLikeCount(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> exhibitionLikeCommandService.addExhibitionLike(exhibition.getId())
            );
            verify(outboxEventPort, never()).save(any());
        }

        @Test
        @DisplayName("아웃박스 이벤트 저장에 실패하면 예외를 던진다")
        public void whenOutboxEventSaveFails() {
            // given
            User user = UserFixture.builder().id(1L).build();
            Exhibition exhibition = ExhibitionFixture.builder().id(1L).build();

            doReturn(user.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(exhibition)).when(exhibitionQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(false).when(exhibitionLikeQueryPort).existsByUserIdAndExhibitionId(any(), any());
            doReturn(null).when(exhibitionLikeCommandPort).save(any());
            doReturn(null).when(outboxEventFactory).exhibitionLiked(any());
            doThrow(new RuntimeException()).when(outboxEventPort).save(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> exhibitionLikeCommandService.addExhibitionLike(exhibition.getId())
            );
        }
    }
}
