package com.benchpress200.photique.singlework.application.command;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.image.domain.port.storage.ImageUploaderPort;
import com.benchpress200.photique.outbox.application.factory.OutboxEventFactory;
import com.benchpress200.photique.outbox.application.port.out.persistence.OutboxEventPort;
import com.benchpress200.photique.outbox.domain.entity.OutboxEvent;
import com.benchpress200.photique.outbox.domain.support.OutboxEventFixture;
import com.benchpress200.photique.singlework.application.command.model.SingleWorkCreateCommand;
import com.benchpress200.photique.singlework.application.command.port.out.event.SingleWorkEventPublishPort;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkCommandPort;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkTagCommandPort;
import com.benchpress200.photique.singlework.application.command.service.SingleWorkCommandService;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkQueryPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkTagQueryPort;
import com.benchpress200.photique.singlework.application.support.fixture.SingleWorkCreateCommandFixture;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkWriterNotFoundException;
import com.benchpress200.photique.support.base.BaseServiceTest;
import com.benchpress200.photique.tag.application.command.port.out.persistence.TagCommandPort;
import com.benchpress200.photique.tag.application.query.port.out.persistence.TagQueryPort;
import com.benchpress200.photique.tag.domain.entity.Tag;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.support.UserFixture;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@DisplayName("단일작품 커맨드 서비스 테스트")
public class SingleWorkCommandServiceTest extends BaseServiceTest {
    @InjectMocks
    private SingleWorkCommandService singleWorkCommandService;

    @Mock
    private AuthenticationUserProviderPort authenticationUserProviderPort;

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private ImageUploaderPort imageUploaderPort;

    @Mock
    private SingleWorkCommandPort singleWorkCommandPort;

    @Mock
    private SingleWorkQueryPort singleWorkQueryPort;

    @Mock
    private SingleWorkTagCommandPort singleWorkTagCommandPort;

    @Mock
    private SingleWorkTagQueryPort singleWorkTagQueryPort;

    @Mock
    private SingleWorkEventPublishPort singleWorkEventPublishPort;

    @Mock
    private TagCommandPort tagCommandPort;

    @Mock
    private TagQueryPort tagQueryPort;

    @Mock
    private OutboxEventFactory outboxEventFactory;

    @Mock
    private OutboxEventPort outboxEventPort;


    @Test
    @DisplayName("단일작품 생성 처리에 성공한다")
    public void postSingleWork_WhenCommandIsValid() {
        //given
        SingleWorkCreateCommand command = SingleWorkCreateCommandFixture.builder().build();
        User user = UserFixture.builder().build();
        Long userId = user.getId();
        String imageUrl = "imageUrl";
        SingleWork singleWork = command.toEntity(user, imageUrl);
        List<String> tagNames = command.getTags();
        List<Tag> tags = tagNames.stream()
                .map(Tag::of)
                .toList();
        List<SingleWorkTag> singleWorkTags = tags.stream()
                .map(tag -> SingleWorkTag.of(singleWork, tag))
                .toList();

        OutboxEvent outboxEvent = OutboxEventFixture.builder().build();

        doReturn(userId).when(authenticationUserProviderPort).getCurrentUserId();
        doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
        doReturn(imageUrl).when(imageUploaderPort).upload(any(), any());
        doNothing().when(singleWorkEventPublishPort).publishSingleWorkImageUploadEvent(any());
        doReturn(singleWork).when(singleWorkCommandPort).save(any());
        doReturn(tags).when(tagQueryPort).findByNameIn(any());
        doReturn(tags).when(tagCommandPort).saveAll(any());
        doReturn(singleWorkTags).when(singleWorkTagCommandPort).saveAll(any());
        doReturn(outboxEvent).when(outboxEventFactory).singleWorkCreated(any(), any());
        doReturn(outboxEvent).when(outboxEventPort).save(any());

        //when
        singleWorkCommandService.postSingleWork(command);

        //then
        verify(authenticationUserProviderPort).getCurrentUserId();
        verify(userQueryPort).findByIdAndDeletedAtIsNull(userId);
        verify(imageUploaderPort).upload(any(), any());
        verify(singleWorkEventPublishPort).publishSingleWorkImageUploadEvent(any());
        verify(singleWorkCommandPort).save(any());
        verify(singleWorkTagCommandPort).saveAll(any());
        verify(outboxEventFactory).singleWorkCreated(any(), any());
        verify(outboxEventPort).save(outboxEvent);
    }

    @Test
    @DisplayName("단일작품 생성 시 작가가 존재하지 않으면 SingleWorkWriterNotFoundException를 던진다")
    public void postSingleWork_whenWriterNotFound() {
        //given
        SingleWorkCreateCommand command = SingleWorkCreateCommandFixture.builder().build();

        doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
        doReturn(Optional.empty()).when(userQueryPort).findByIdAndDeletedAtIsNull(any());

        //when & then
        assertThrows(
                SingleWorkWriterNotFoundException.class,
                () -> singleWorkCommandService.postSingleWork(command)
        );
    }
}
