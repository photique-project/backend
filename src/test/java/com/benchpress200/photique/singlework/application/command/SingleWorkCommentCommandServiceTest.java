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
import com.benchpress200.photique.singlework.application.command.model.SingleWorkCommentCreateCommand;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkCommentCommandPort;
import com.benchpress200.photique.singlework.application.command.service.SingleWorkCommentCommandService;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkCommentQueryPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkQueryPort;
import com.benchpress200.photique.singlework.application.support.fixture.SingleWorkCommentCreateCommandFixture;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
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

@DisplayName("단일작품 댓글 커맨드 서비스 테스트")
public class SingleWorkCommentCommandServiceTest extends BaseServiceTest {
    @InjectMocks
    private SingleWorkCommentCommandService singleWorkCommentCommandService;

    @Mock
    private AuthenticationUserProviderPort authenticationUserProvider;

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private SingleWorkQueryPort singleWorkQueryPort;

    @Mock
    private SingleWorkCommentQueryPort singleWorkCommentQueryPort;

    @Mock
    private SingleWorkCommentCommandPort singleWorkCommentCommandPort;

    @Mock
    private OutboxEventFactory outboxEventFactory;

    @Mock
    private OutboxEventPort outboxEventPort;

    @Nested
    @DisplayName("단일작품 댓글 생성")
    class CreateSingleWorkCommentTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenCommandValid() {
            // given
            User writer = UserFixture.builder().id(1L).build();
            SingleWork singleWork = SingleWorkFixture.builder().build();
            SingleWorkCommentCreateCommand command = SingleWorkCommentCreateCommandFixture.builder().build();
            OutboxEvent outboxEvent = OutboxEventFixture.builder().build();

            doReturn(writer.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(writer)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(singleWork)).when(singleWorkQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(null).when(singleWorkCommentCommandPort).save(any());
            doReturn(outboxEvent).when(outboxEventFactory).singleWorkCommentCreated(any());
            doReturn(outboxEvent).when(outboxEventPort).save(any());

            // when
            singleWorkCommentCommandService.createSingleWorkComment(command);

            // then
            verify(userQueryPort).findByIdAndDeletedAtIsNull(writer.getId());
            verify(singleWorkQueryPort).findByIdAndDeletedAtIsNull(command.getSingleWorkId());
            verify(singleWorkCommentCommandPort).save(any());
            verify(outboxEventFactory).singleWorkCommentCreated(any());
            verify(outboxEventPort).save(outboxEvent);
        }

        @Test
        @DisplayName("유저가 존재하지 않으면 UserNotFoundException을 던진다")
        public void whenUserNotFound() {
            // given
            SingleWorkCommentCreateCommand command = SingleWorkCommentCreateCommandFixture.builder().build();

            doReturn(1L).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.empty()).when(userQueryPort).findByIdAndDeletedAtIsNull(any());

            // when & then
            assertThrows(
                    UserNotFoundException.class,
                    () -> singleWorkCommentCommandService.createSingleWorkComment(command)
            );
            verify(singleWorkCommentCommandPort, never()).save(any());
        }

        @Test
        @DisplayName("단일작품이 존재하지 않으면 SingleWorkNotFoundException을 던진다")
        public void whenSingleWorkNotFound() {
            // given
            User writer = UserFixture.builder().id(1L).build();
            SingleWorkCommentCreateCommand command = SingleWorkCommentCreateCommandFixture.builder().build();

            doReturn(writer.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(writer)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.empty()).when(singleWorkQueryPort).findByIdAndDeletedAtIsNull(any());

            // when & then
            assertThrows(
                    SingleWorkNotFoundException.class,
                    () -> singleWorkCommentCommandService.createSingleWorkComment(command)
            );
            verify(singleWorkCommentCommandPort, never()).save(any());
        }

        @Test
        @DisplayName("댓글 저장에 실패하면 예외를 던진다")
        public void whenSaveFails() {
            // given
            User writer = UserFixture.builder().id(1L).build();
            SingleWork singleWork = SingleWorkFixture.builder().build();
            SingleWorkCommentCreateCommand command = SingleWorkCommentCreateCommandFixture.builder().build();

            doReturn(writer.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(writer)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(singleWork)).when(singleWorkQueryPort).findByIdAndDeletedAtIsNull(any());
            doThrow(new RuntimeException()).when(singleWorkCommentCommandPort).save(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> singleWorkCommentCommandService.createSingleWorkComment(command)
            );
            verify(outboxEventPort, never()).save(any());
        }

        @Test
        @DisplayName("아웃박스 이벤트 저장에 실패하면 예외를 던진다")
        public void whenOutboxEventSaveFails() {
            // given
            User writer = UserFixture.builder().id(1L).build();
            SingleWork singleWork = SingleWorkFixture.builder().build();
            SingleWorkCommentCreateCommand command = SingleWorkCommentCreateCommandFixture.builder().build();

            doReturn(writer.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(writer)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(singleWork)).when(singleWorkQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(null).when(singleWorkCommentCommandPort).save(any());
            doReturn(null).when(outboxEventFactory).singleWorkCommentCreated(any());
            doThrow(new RuntimeException()).when(outboxEventPort).save(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> singleWorkCommentCommandService.createSingleWorkComment(command)
            );
        }
    }
}
