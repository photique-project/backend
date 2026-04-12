package com.benchpress200.photique.exhibition.application.command;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionBookmarkCommandPort;
import com.benchpress200.photique.exhibition.application.command.service.ExhibitionBookmarkCommandService;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionBookmarkQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionQueryPort;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionAlreadyBookmarkedException;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionNotFoundException;
import com.benchpress200.photique.exhibition.domain.support.ExhibitionFixture;
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

@DisplayName("전시회 북마크 커맨드 서비스 테스트")
public class ExhibitionBookmarkCommandServiceTest extends BaseServiceTest {
    @InjectMocks
    private ExhibitionBookmarkCommandService exhibitionBookmarkCommandService;

    @Mock
    private AuthenticationUserProviderPort authenticationUserProvider;

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private ExhibitionQueryPort exhibitionQueryPort;

    @Mock
    private ExhibitionBookmarkQueryPort exhibitionBookmarkQueryPort;

    @Mock
    private ExhibitionBookmarkCommandPort exhibitionBookmarkCommandPort;

    @Nested
    @DisplayName("전시회 북마크 추가")
    class AddExhibitionBookmarkTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenCommandValid() {
            // given
            User user = UserFixture.builder().id(1L).build();
            Exhibition exhibition = ExhibitionFixture.builder().id(1L).build();

            doReturn(user.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(exhibition)).when(exhibitionQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(false).when(exhibitionBookmarkQueryPort).existsByUserIdAndExhibitionId(any(), any());
            doReturn(null).when(exhibitionBookmarkCommandPort).save(any());

            // when
            exhibitionBookmarkCommandService.addExhibitionBookmark(exhibition.getId());

            // then
            verify(userQueryPort).findByIdAndDeletedAtIsNull(user.getId());
            verify(exhibitionQueryPort).findByIdAndDeletedAtIsNull(exhibition.getId());
            verify(exhibitionBookmarkQueryPort).existsByUserIdAndExhibitionId(user.getId(), exhibition.getId());
            verify(exhibitionBookmarkCommandPort).save(any());
        }

        @Test
        @DisplayName("유저가 존재하지 않으면 UserNotFoundException을 던진다")
        public void whenUserNotFound() {
            // given
            Exhibition exhibition = ExhibitionFixture.builder().id(1L).build();

            doReturn(1L).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.empty()).when(userQueryPort).findByIdAndDeletedAtIsNull(any());

            // when & then
            assertThrows(
                    UserNotFoundException.class,
                    () -> exhibitionBookmarkCommandService.addExhibitionBookmark(exhibition.getId())
            );
            verify(exhibitionBookmarkCommandPort, never()).save(any());
        }

        @Test
        @DisplayName("전시회가 존재하지 않으면 ExhibitionNotFoundException을 던진다")
        public void whenExhibitionNotFound() {
            // given
            User user = UserFixture.builder().id(1L).build();
            Exhibition exhibition = ExhibitionFixture.builder().id(1L).build();

            doReturn(user.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.empty()).when(exhibitionQueryPort).findByIdAndDeletedAtIsNull(any());

            // when & then
            assertThrows(
                    ExhibitionNotFoundException.class,
                    () -> exhibitionBookmarkCommandService.addExhibitionBookmark(exhibition.getId())
            );
            verify(exhibitionBookmarkCommandPort, never()).save(any());
        }

        @Test
        @DisplayName("이미 북마크한 전시회이면 ExhibitionAlreadyBookmarkedException을 던진다")
        public void whenAlreadyBookmarked() {
            // given
            User user = UserFixture.builder().id(1L).build();
            Exhibition exhibition = ExhibitionFixture.builder().id(1L).build();

            doReturn(user.getId()).when(authenticationUserProvider).getCurrentUserId();
            doReturn(Optional.of(user)).when(userQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(Optional.of(exhibition)).when(exhibitionQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(true).when(exhibitionBookmarkQueryPort).existsByUserIdAndExhibitionId(any(), any());

            // when & then
            assertThrows(
                    ExhibitionAlreadyBookmarkedException.class,
                    () -> exhibitionBookmarkCommandService.addExhibitionBookmark(exhibition.getId())
            );
            verify(exhibitionBookmarkCommandPort, never()).save(any());
        }
    }
}
