package com.benchpress200.photique.exhibition.application.command;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.exhibition.application.command.model.ExhibitionCreateCommand;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionCommandPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionEventPublishPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionTagCommandPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionWorkCommandPort;
import com.benchpress200.photique.exhibition.application.command.service.ExhibitionCommandService;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionTagQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionWorkQueryPort;
import com.benchpress200.photique.exhibition.application.support.fixture.ExhibitionCreateCommandFixture;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.support.ExhibitionFixture;
import com.benchpress200.photique.image.domain.port.storage.ImageUploaderPort;
import com.benchpress200.photique.outbox.application.factory.OutboxEventFactory;
import com.benchpress200.photique.outbox.application.port.out.persistence.OutboxEventPort;
import com.benchpress200.photique.outbox.domain.entity.OutboxEvent;
import com.benchpress200.photique.outbox.domain.support.OutboxEventFixture;
import com.benchpress200.photique.support.base.BaseServiceTest;
import com.benchpress200.photique.tag.application.command.port.out.persistence.TagCommandPort;
import com.benchpress200.photique.tag.application.query.port.out.persistence.TagQueryPort;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.exception.UserNotFoundException;
import com.benchpress200.photique.user.domain.support.UserFixture;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@DisplayName("전시회 커맨드 서비스 테스트")
public class ExhibitionCommandServiceTest extends BaseServiceTest {
    @InjectMocks
    private ExhibitionCommandService exhibitionCommandService;

    @Mock
    private AuthenticationUserProviderPort authenticationUserProviderPort;

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private ImageUploaderPort imageUploaderPort;

    @Mock
    private ExhibitionEventPublishPort exhibitionEventPublishPort;

    @Mock
    private ExhibitionCommandPort exhibitionCommandPort;

    @Mock
    private ExhibitionQueryPort exhibitionQueryPort;

    @Mock
    private ExhibitionTagCommandPort exhibitionTagCommandPort;

    @Mock
    private ExhibitionTagQueryPort exhibitionTagQueryPort;

    @Mock
    private ExhibitionWorkCommandPort exhibitionWorkCommandPort;

    @Mock
    private ExhibitionWorkQueryPort exhibitionWorkQueryPort;

    @Mock
    private TagCommandPort tagCommandPort;

    @Mock
    private TagQueryPort tagQueryPort;

    @Mock
    private OutboxEventFactory outboxEventFactory;

    @Mock
    private OutboxEventPort outboxEventPort;

    @Nested
    @DisplayName("전시회 생성")
    class OpenExhibitionTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenCommandValid() {
            // given
            User writer = UserFixture.builder().id(1L).build();
            ExhibitionCreateCommand command = ExhibitionCreateCommandFixture.builder().build();
            Exhibition savedExhibition = ExhibitionFixture.builder().id(1L).build();
            String imageUrl = "https://test-bucket/exhibition/test-image.jpg";
            OutboxEvent outboxEvent = OutboxEventFixture.builder().build();

            doReturn(writer.getId()).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Optional.of(writer)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(savedExhibition).when(exhibitionCommandPort).save(any());
            doReturn(imageUrl).when(imageUploaderPort).upload(any(), any());
            doNothing().when(exhibitionEventPublishPort).publishExhibitionWorkImageUploadEvent(any());
            doReturn(null).when(exhibitionWorkCommandPort).save(any());
            doReturn(List.of()).when(tagQueryPort).findByNameIn(any());
            doReturn(List.of()).when(tagCommandPort).saveAll(any());
            doReturn(List.of()).when(exhibitionTagCommandPort).saveAll(any());
            doReturn(outboxEvent).when(outboxEventFactory).exhibitionCreated(any(), any());
            doReturn(outboxEvent).when(outboxEventPort).save(any());

            // when
            exhibitionCommandService.openExhibition(command);

            // then
            verify(userQueryPort).findByIdAndDeletedAtIsNull(writer.getId());
            verify(exhibitionCommandPort).save(any());
            verify(imageUploaderPort).upload(any(), any());
            verify(exhibitionEventPublishPort).publishExhibitionWorkImageUploadEvent(any());
            verify(exhibitionWorkCommandPort).save(any());
            verify(exhibitionTagCommandPort).saveAll(any());
            verify(outboxEventFactory).exhibitionCreated(any(), any());
            verify(outboxEventPort).save(outboxEvent);
        }

        @Test
        @DisplayName("작가가 존재하지 않으면 UserNotFoundException을 던진다")
        public void whenUserNotFound() {
            // given
            ExhibitionCreateCommand command = ExhibitionCreateCommandFixture.builder().build();

            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Optional.empty()).when(userQueryPort).findByIdAndDeletedAtIsNull(any());

            // when & then
            assertThrows(
                    UserNotFoundException.class,
                    () -> exhibitionCommandService.openExhibition(command)
            );
            verify(exhibitionCommandPort, never()).save(any());
        }

        @Test
        @DisplayName("전시회 저장에 실패하면 예외를 던지고 이후 처리를 진행하지 않는다")
        public void whenExhibitionSaveFailed() {
            // given
            User writer = UserFixture.builder().id(1L).build();
            ExhibitionCreateCommand command = ExhibitionCreateCommandFixture.builder().build();

            doReturn(writer.getId()).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Optional.of(writer)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doThrow(new RuntimeException()).when(exhibitionCommandPort).save(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> exhibitionCommandService.openExhibition(command)
            );
            verify(exhibitionCommandPort).save(any());
            verify(imageUploaderPort, never()).upload(any(), any());
            verify(exhibitionWorkCommandPort, never()).save(any());
            verify(exhibitionTagCommandPort, never()).saveAll(any());
            verify(outboxEventPort, never()).save(any());
        }

        @Test
        @DisplayName("이미지 업로드에 실패하면 예외를 던지고 이후 처리를 진행하지 않는다")
        public void whenImageUploadFailed() {
            // given
            User writer = UserFixture.builder().id(1L).build();
            ExhibitionCreateCommand command = ExhibitionCreateCommandFixture.builder().build();
            Exhibition savedExhibition = ExhibitionFixture.builder().id(1L).build();

            doReturn(writer.getId()).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Optional.of(writer)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(savedExhibition).when(exhibitionCommandPort).save(any());
            doThrow(new RuntimeException()).when(imageUploaderPort).upload(any(), any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> exhibitionCommandService.openExhibition(command)
            );
            verify(exhibitionCommandPort).save(any());
            verify(imageUploaderPort).upload(any(), any());
            verify(exhibitionWorkCommandPort, never()).save(any());
            verify(exhibitionTagCommandPort, never()).saveAll(any());
            verify(outboxEventPort, never()).save(any());
        }

        @Test
        @DisplayName("전시회 개별 작품 저장에 실패하면 예외를 던지고 이후 처리를 진행하지 않는다")
        public void whenExhibitionWorkSaveFailed() {
            // given
            User writer = UserFixture.builder().id(1L).build();
            ExhibitionCreateCommand command = ExhibitionCreateCommandFixture.builder().build();
            Exhibition savedExhibition = ExhibitionFixture.builder().id(1L).build();
            String imageUrl = "https://test-bucket/exhibition/test-image.jpg";

            doReturn(writer.getId()).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Optional.of(writer)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(savedExhibition).when(exhibitionCommandPort).save(any());
            doReturn(imageUrl).when(imageUploaderPort).upload(any(), any());
            doNothing().when(exhibitionEventPublishPort).publishExhibitionWorkImageUploadEvent(any());
            doThrow(new RuntimeException()).when(exhibitionWorkCommandPort).save(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> exhibitionCommandService.openExhibition(command)
            );
            verify(exhibitionCommandPort).save(any());
            verify(imageUploaderPort).upload(any(), any());
            verify(exhibitionWorkCommandPort).save(any());
            verify(exhibitionTagCommandPort, never()).saveAll(any());
            verify(outboxEventPort, never()).save(any());
        }

        @Test
        @DisplayName("태그 저장에 실패하면 예외를 던지고 아웃박스 이벤트 저장을 진행하지 않는다")
        public void whenTagSaveFailed() {
            // given
            User writer = UserFixture.builder().id(1L).build();
            ExhibitionCreateCommand command = ExhibitionCreateCommandFixture.builder().build();
            Exhibition savedExhibition = ExhibitionFixture.builder().id(1L).build();
            String imageUrl = "https://test-bucket/exhibition/test-image.jpg";

            doReturn(writer.getId()).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Optional.of(writer)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(savedExhibition).when(exhibitionCommandPort).save(any());
            doReturn(imageUrl).when(imageUploaderPort).upload(any(), any());
            doNothing().when(exhibitionEventPublishPort).publishExhibitionWorkImageUploadEvent(any());
            doReturn(null).when(exhibitionWorkCommandPort).save(any());
            doReturn(List.of()).when(tagQueryPort).findByNameIn(any());
            doReturn(List.of()).when(tagCommandPort).saveAll(any());
            doThrow(new RuntimeException()).when(exhibitionTagCommandPort).saveAll(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> exhibitionCommandService.openExhibition(command)
            );
            verify(exhibitionWorkCommandPort).save(any());
            verify(exhibitionTagCommandPort).saveAll(any());
            verify(outboxEventPort, never()).save(any());
        }

        @Test
        @DisplayName("아웃박스 이벤트 저장에 실패하면 예외를 던진다")
        public void whenOutboxEventSaveFailed() {
            // given
            User writer = UserFixture.builder().id(1L).build();
            ExhibitionCreateCommand command = ExhibitionCreateCommandFixture.builder().build();
            Exhibition savedExhibition = ExhibitionFixture.builder().id(1L).build();
            String imageUrl = "https://test-bucket/exhibition/test-image.jpg";
            OutboxEvent outboxEvent = OutboxEventFixture.builder().build();

            doReturn(writer.getId()).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Optional.of(writer)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(savedExhibition).when(exhibitionCommandPort).save(any());
            doReturn(imageUrl).when(imageUploaderPort).upload(any(), any());
            doNothing().when(exhibitionEventPublishPort).publishExhibitionWorkImageUploadEvent(any());
            doReturn(null).when(exhibitionWorkCommandPort).save(any());
            doReturn(List.of()).when(tagQueryPort).findByNameIn(any());
            doReturn(List.of()).when(tagCommandPort).saveAll(any());
            doReturn(List.of()).when(exhibitionTagCommandPort).saveAll(any());
            doReturn(outboxEvent).when(outboxEventFactory).exhibitionCreated(any(), any());
            doThrow(new RuntimeException()).when(outboxEventPort).save(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> exhibitionCommandService.openExhibition(command)
            );
            verify(exhibitionCommandPort).save(any());
            verify(exhibitionWorkCommandPort).save(any());
            verify(exhibitionTagCommandPort).saveAll(any());
            verify(outboxEventPort).save(any());
        }
    }
}
