package com.benchpress200.photique.singlework.application.query;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkCommandPort;
import com.benchpress200.photique.singlework.application.query.port.out.event.SingleWorkViewCountPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkLikeQueryPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkQueryPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkTagQueryPort;
import com.benchpress200.photique.singlework.application.query.result.SingleWorkDetailsResult;
import com.benchpress200.photique.singlework.application.query.service.SingleWorkQueryService;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkNotFoundException;
import com.benchpress200.photique.singlework.domain.support.SingleWorkFixture;
import com.benchpress200.photique.support.base.BaseServiceTest;
import com.benchpress200.photique.user.application.query.port.out.persistence.FollowQueryPort;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataAccessResourceFailureException;

@DisplayName("단일작품 쿼리 서비스 테스트")
public class SingleWorkQueryServiceTest extends BaseServiceTest {
    @InjectMocks
    private SingleWorkQueryService singleWorkQueryService;

    @Mock
    private AuthenticationUserProviderPort authenticationUserProviderPort;

    @Mock
    private SingleWorkViewCountPort singleWorkViewCountPort;

    @Mock
    private SingleWorkCommandPort singleWorkCommandPort;

    @Mock
    private SingleWorkQueryPort singleWorkQueryPort;

    @Mock
    private SingleWorkTagQueryPort singleWorkTagQueryPort;

    @Mock
    private SingleWorkLikeQueryPort singleWorkLikeQueryPort;

    @Mock
    private FollowQueryPort followQueryPort;

    @Nested
    @DisplayName("단일작품 상세 조회")
    class GetSingleWorkDetailsTest {
        @Test
        @DisplayName("인증된 유저 요청 시 처리에 성공한다")
        public void whenAuthenticated() {
            // given
            SingleWork singleWork = SingleWorkFixture.builder().build();

            doReturn(Optional.of(singleWork)).when(singleWorkQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(List.of()).when(singleWorkTagQueryPort).findBySingleWorkWithTag(any());
            doReturn(true).when(authenticationUserProviderPort).isAuthenticated();
            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(false).when(singleWorkLikeQueryPort).existsByUserIdAndSingleWorkId(any(), any());
            doReturn(false).when(followQueryPort).existsByFollowerIdAndFolloweeId(any(), any());

            // when
            SingleWorkDetailsResult result = singleWorkQueryService.getSingleWorkDetails(1L);

            // then
            verify(singleWorkQueryPort).findByIdAndDeletedAtIsNull(1L);
            verify(singleWorkTagQueryPort).findBySingleWorkWithTag(singleWork);
            verify(singleWorkLikeQueryPort).existsByUserIdAndSingleWorkId(any(), any());
            verify(followQueryPort).existsByFollowerIdAndFolloweeId(any(), any());
            verify(singleWorkViewCountPort).incrementViewCount(1L);
            assertNotNull(result);
        }

        @Test
        @DisplayName("비인증 유저 요청 시 처리에 성공한다")
        public void whenNotAuthenticated() {
            // given
            SingleWork singleWork = SingleWorkFixture.builder().build();

            doReturn(Optional.of(singleWork)).when(singleWorkQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(List.of()).when(singleWorkTagQueryPort).findBySingleWorkWithTag(any());
            doReturn(false).when(authenticationUserProviderPort).isAuthenticated();

            // when
            SingleWorkDetailsResult result = singleWorkQueryService.getSingleWorkDetails(1L);

            // then
            verify(singleWorkLikeQueryPort, never()).existsByUserIdAndSingleWorkId(any(), any());
            verify(followQueryPort, never()).existsByFollowerIdAndFolloweeId(any(), any());
            verify(singleWorkViewCountPort).incrementViewCount(1L);
            assertNotNull(result);
        }

        @Test
        @DisplayName("단일작품이 존재하지 않으면 SingleWorkNotFoundException을 던진다")
        public void whenSingleWorkNotFound() {
            // given
            doReturn(Optional.empty()).when(singleWorkQueryPort).findByIdAndDeletedAtIsNull(any());

            // when & then
            assertThrows(
                    SingleWorkNotFoundException.class,
                    () -> singleWorkQueryService.getSingleWorkDetails(1L)
            );
            verify(singleWorkTagQueryPort, never()).findBySingleWorkWithTag(any());
        }

        @Test
        @DisplayName("태그 조회에 실패하면 예외를 던진다")
        public void whenFindTagsFails() {
            // given
            SingleWork singleWork = SingleWorkFixture.builder().build();

            doReturn(Optional.of(singleWork)).when(singleWorkQueryPort).findByIdAndDeletedAtIsNull(any());
            doThrow(new RuntimeException()).when(singleWorkTagQueryPort).findBySingleWorkWithTag(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> singleWorkQueryService.getSingleWorkDetails(1L)
            );
            verify(singleWorkViewCountPort, never()).incrementViewCount(any());
        }

        @Test
        @DisplayName("좋아요 여부 조회에 실패하면 예외를 던진다")
        public void whenIsLikedCheckFails() {
            // given
            SingleWork singleWork = SingleWorkFixture.builder().build();

            doReturn(Optional.of(singleWork)).when(singleWorkQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(List.of()).when(singleWorkTagQueryPort).findBySingleWorkWithTag(any());
            doReturn(true).when(authenticationUserProviderPort).isAuthenticated();
            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doThrow(new RuntimeException()).when(singleWorkLikeQueryPort).existsByUserIdAndSingleWorkId(any(), any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> singleWorkQueryService.getSingleWorkDetails(1L)
            );
            verify(singleWorkViewCountPort, never()).incrementViewCount(any());
        }

        @Test
        @DisplayName("팔로우 여부 조회에 실패하면 예외를 던진다")
        public void whenIsFollowingCheckFails() {
            // given
            SingleWork singleWork = SingleWorkFixture.builder().build();

            doReturn(Optional.of(singleWork)).when(singleWorkQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(List.of()).when(singleWorkTagQueryPort).findBySingleWorkWithTag(any());
            doReturn(true).when(authenticationUserProviderPort).isAuthenticated();
            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(false).when(singleWorkLikeQueryPort).existsByUserIdAndSingleWorkId(any(), any());
            doThrow(new RuntimeException()).when(followQueryPort).existsByFollowerIdAndFolloweeId(any(), any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> singleWorkQueryService.getSingleWorkDetails(1L)
            );
            verify(singleWorkViewCountPort, never()).incrementViewCount(any());
        }

        @Test
        @DisplayName("조회수 증가 실패 시 DB로 fallback 처리한다")
        public void whenViewCountFallback() {
            // given
            SingleWork singleWork = SingleWorkFixture.builder().build();

            doReturn(Optional.of(singleWork)).when(singleWorkQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(List.of()).when(singleWorkTagQueryPort).findBySingleWorkWithTag(any());
            doReturn(false).when(authenticationUserProviderPort).isAuthenticated();
            doThrow(new DataAccessResourceFailureException("Redis 장애")).when(singleWorkViewCountPort).incrementViewCount(any());

            // when
            SingleWorkDetailsResult result = singleWorkQueryService.getSingleWorkDetails(1L);

            // then
            verify(singleWorkCommandPort).incrementViewCount(1L);
            assertNotNull(result);
        }

        @Test
        @DisplayName("조회수 증가 fallback도 실패하면 예외를 던진다")
        public void whenViewCountFallbackFails() {
            // given
            SingleWork singleWork = SingleWorkFixture.builder().build();

            doReturn(Optional.of(singleWork)).when(singleWorkQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(List.of()).when(singleWorkTagQueryPort).findBySingleWorkWithTag(any());
            doReturn(false).when(authenticationUserProviderPort).isAuthenticated();
            doThrow(new DataAccessResourceFailureException("Redis 장애")).when(singleWorkViewCountPort).incrementViewCount(any());
            doThrow(new RuntimeException()).when(singleWorkCommandPort).incrementViewCount(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> singleWorkQueryService.getSingleWorkDetails(1L)
            );
        }
    }
}
