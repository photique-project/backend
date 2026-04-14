package com.benchpress200.photique.exhibition.application.command;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.exhibition.application.command.model.ExhibitionCommentCreateCommand;
import com.benchpress200.photique.exhibition.application.command.model.ExhibitionCommentUpdateCommand;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionCommentCommandPort;
import com.benchpress200.photique.exhibition.application.command.service.ExhibitionCommentCommandService;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionCommentQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionQueryPort;
import com.benchpress200.photique.exhibition.application.support.fixture.ExhibitionCommentCreateCommandFixture;
import com.benchpress200.photique.exhibition.application.support.fixture.ExhibitionCommentUpdateCommandFixture;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionCommentNotFoundException;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionCommentNotOwnedException;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionNotFoundException;
import com.benchpress200.photique.exhibition.domain.support.ExhibitionCommentFixture;
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

@DisplayName("전시회 감상평 커맨드 서비스 테스트")
public class ExhibitionCommentCommandServiceTest extends BaseServiceTest {
    @InjectMocks
    private ExhibitionCommentCommandService exhibitionCommentCommandService;

    @Mock
    private AuthenticationUserProviderPort authenticationUserProvider;

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private ExhibitionQueryPort exhibitionQueryPort;

    @Mock
    private ExhibitionCommentQueryPort exhibitionCommentQueryPort;

    @Mock
    private ExhibitionCommentCommandPort exhibitionCommentCommandPort;

    @Mock
    private OutboxEventFactory outboxEventFactory;

    @Mock
    private OutboxEventPort outboxEventPort;

    @Nested
    @DisplayName("전시회 감상평 생성")
    class CreateExhibitionCommentTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenCommandValid() {
            // given
            User writer = UserFixture.builder().id(1L).build();
            Exhibition exhibition = ExhibitionFixture.builder().id(1L).build();
            ExhibitionCommentCreateCommand command = ExhibitionCommentCreateCommandFixture.builder().build();
            OutboxEvent outboxEvent = OutboxEventFixture.builder().build();

            doReturn(writer.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(writer)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(exhibition)).when(exhibitionQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(null).when(exhibitionCommentCommandPort).save(any());
            doReturn(outboxEvent).when(outboxEventFactory).exhibitionCommentCreated(any());
            doReturn(outboxEvent).when(outboxEventPort).save(any());

            // when
            exhibitionCommentCommandService.createExhibitionComment(command);

            // then
            verify(userQueryPort).findByIdAndDeletedAtIsNull(writer.getId());
            verify(exhibitionQueryPort).findByIdAndDeletedAtIsNull(command.getExhibitionId());
            verify(exhibitionCommentCommandPort).save(any());
            verify(outboxEventFactory).exhibitionCommentCreated(any());
            verify(outboxEventPort).save(outboxEvent);
        }

        @Test
        @DisplayName("유저가 존재하지 않으면 UserNotFoundException을 던진다")
        public void whenUserNotFound() {
            // given
            ExhibitionCommentCreateCommand command = ExhibitionCommentCreateCommandFixture.builder().build();

            doReturn(1L).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.empty()).when(userQueryPort).findByIdAndDeletedAtIsNull(any());

            // when & then
            assertThrows(
                    UserNotFoundException.class,
                    () -> exhibitionCommentCommandService.createExhibitionComment(command)
            );
            verify(exhibitionCommentCommandPort, never()).save(any());
        }

        @Test
        @DisplayName("감상평 저장에 실패하면 예외를 던진다")
        public void whenSaveFails() {
            // given
            User writer = UserFixture.builder().id(1L).build();
            Exhibition exhibition = ExhibitionFixture.builder().id(1L).build();
            ExhibitionCommentCreateCommand command = ExhibitionCommentCreateCommandFixture.builder().build();

            doReturn(writer.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(writer)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(exhibition)).when(exhibitionQueryPort).findByIdAndDeletedAtIsNull(any());
            doThrow(new RuntimeException()).when(exhibitionCommentCommandPort).save(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> exhibitionCommentCommandService.createExhibitionComment(command)
            );
            verify(outboxEventPort, never()).save(any());
        }

        @Test
        @DisplayName("전시회가 존재하지 않으면 ExhibitionNotFoundException을 던진다")
        public void whenExhibitionNotFound() {
            // given
            User writer = UserFixture.builder().id(1L).build();
            ExhibitionCommentCreateCommand command = ExhibitionCommentCreateCommandFixture.builder().build();

            doReturn(writer.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(writer)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.empty()).when(exhibitionQueryPort).findByIdAndDeletedAtIsNull(any());

            // when & then
            assertThrows(
                    ExhibitionNotFoundException.class,
                    () -> exhibitionCommentCommandService.createExhibitionComment(command)
            );
            verify(exhibitionCommentCommandPort, never()).save(any());
        }
    }

    @Nested
    @DisplayName("전시회 감상평 수정")
    class UpdateExhibitionCommentTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenCommandValid() {
            // given
            User writer = UserFixture.builder().id(1L).build();
            ExhibitionComment exhibitionComment = ExhibitionCommentFixture.builder().writer(writer).build();
            ExhibitionCommentUpdateCommand command = ExhibitionCommentUpdateCommandFixture.builder().build();

            doReturn(Optional.of(exhibitionComment)).when(exhibitionCommentQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(writer.getId()).when(authenticationUserProvider).getCurrentUserId();

            // when
            exhibitionCommentCommandService.updateExhibitionComment(command);

            // then
            verify(exhibitionCommentQueryPort).findByIdAndDeletedAtIsNull(command.getCommentId());
            verify(authenticationUserProvider).getCurrentUserId();
        }

        @Test
        @DisplayName("감상평이 존재하지 않으면 ExhibitionCommentNotFoundException을 던진다")
        public void whenCommentNotFound() {
            // given
            ExhibitionCommentUpdateCommand command = ExhibitionCommentUpdateCommandFixture.builder().build();

            doReturn(Optional.empty()).when(exhibitionCommentQueryPort).findByIdAndDeletedAtIsNull(any());

            // when & then
            assertThrows(
                    ExhibitionCommentNotFoundException.class,
                    () -> exhibitionCommentCommandService.updateExhibitionComment(command)
            );
        }

        @Test
        @DisplayName("감상평 소유자가 아니면 ExhibitionCommentNotOwnedException을 던진다")
        public void whenNotOwner() {
            // given
            User writer = UserFixture.builder().id(1L).build();
            ExhibitionComment exhibitionComment = ExhibitionCommentFixture.builder().writer(writer).build();
            ExhibitionCommentUpdateCommand command = ExhibitionCommentUpdateCommandFixture.builder().build();

            doReturn(Optional.of(exhibitionComment)).when(exhibitionCommentQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(2L).when(authenticationUserProvider).getCurrentUserId();

            // when & then
            assertThrows(
                    ExhibitionCommentNotOwnedException.class,
                    () -> exhibitionCommentCommandService.updateExhibitionComment(command)
            );
        }
    }

    @Nested
    @DisplayName("전시회 감상평 삭제")
    class DeleteExhibitionCommentTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenCommandValid() {
            // given
            User writer = UserFixture.builder().id(1L).build();
            ExhibitionComment exhibitionComment = ExhibitionCommentFixture.builder().writer(writer).build();

            doReturn(Optional.of(exhibitionComment)).when(exhibitionCommentQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(writer.getId()).when(authenticationUserProvider).getCurrentUserId();

            // when
            exhibitionCommentCommandService.deleteExhibitionComment(1L);

            // then
            verify(exhibitionCommentQueryPort).findByIdAndDeletedAtIsNull(1L);
            verify(authenticationUserProvider).getCurrentUserId();
        }

        @Test
        @DisplayName("감상평이 존재하지 않으면 아무 처리도 하지 않는다")
        public void whenCommentNotFound() {
            // given
            doReturn(Optional.empty()).when(exhibitionCommentQueryPort).findByIdAndDeletedAtIsNull(any());

            // when
            exhibitionCommentCommandService.deleteExhibitionComment(1L);

            // then
            verify(authenticationUserProvider, never()).getCurrentUserId();
        }

        @Test
        @DisplayName("감상평 소유자가 아니면 ExhibitionCommentNotOwnedException을 던진다")
        public void whenNotOwner() {
            // given
            User writer = UserFixture.builder().id(1L).build();
            ExhibitionComment exhibitionComment = ExhibitionCommentFixture.builder().writer(writer).build();

            doReturn(Optional.of(exhibitionComment)).when(exhibitionCommentQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(2L).when(authenticationUserProvider).getCurrentUserId();

            // when & then
            assertThrows(
                    ExhibitionCommentNotOwnedException.class,
                    () -> exhibitionCommentCommandService.deleteExhibitionComment(1L)
            );
        }
    }
}
