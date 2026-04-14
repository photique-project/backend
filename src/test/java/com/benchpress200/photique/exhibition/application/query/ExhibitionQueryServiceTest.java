package com.benchpress200.photique.exhibition.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionCommandPort;
import com.benchpress200.photique.exhibition.application.query.port.out.event.ExhibitionViewCountPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionBookmarkQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionLikeQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionTagQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionWorkQueryPort;
import com.benchpress200.photique.exhibition.application.query.model.ExhibitionSearchQuery;
import com.benchpress200.photique.exhibition.application.query.result.ExhibitionDetailsResult;
import com.benchpress200.photique.exhibition.application.query.result.ExhibitionSearchResult;
import com.benchpress200.photique.exhibition.application.query.service.ExhibitionQueryService;
import com.benchpress200.photique.exhibition.application.query.support.fixture.ExhibitionSearchQueryFixture;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionNotFoundException;
import com.benchpress200.photique.exhibition.domain.support.ExhibitionFixture;
import com.benchpress200.photique.singlework.application.query.model.MyExhibitionSearchQuery;
import com.benchpress200.photique.singlework.application.query.result.MyExhibitionSearchResult;
import com.benchpress200.photique.singlework.application.query.support.fixture.MyExhibitionSearchQueryFixture;
import com.benchpress200.photique.support.base.BaseServiceTest;
import com.benchpress200.photique.user.application.query.port.out.persistence.FollowQueryPort;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;

@DisplayName("전시회 쿼리 서비스 테스트")
public class ExhibitionQueryServiceTest extends BaseServiceTest {
    @InjectMocks
    private ExhibitionQueryService exhibitionQueryService;

    @Mock
    private AuthenticationUserProviderPort authenticationUserProviderPort;

    @Mock
    private FollowQueryPort followQueryPort;

    @Mock
    private ExhibitionViewCountPort exhibitionViewCountPort;

    @Mock
    private ExhibitionCommandPort exhibitionCommandPort;

    @Mock
    private ExhibitionQueryPort exhibitionQueryPort;

    @Mock
    private ExhibitionTagQueryPort exhibitionTagQueryPort;

    @Mock
    private ExhibitionWorkQueryPort exhibitionWorkQueryPort;

    @Mock
    private ExhibitionLikeQueryPort exhibitionLikeQueryPort;

    @Mock
    private ExhibitionBookmarkQueryPort exhibitionBookmarkQueryPort;

    @Nested
    @DisplayName("전시회 상세 조회")
    class GetExhibitionDetailsTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenQueryValid() {
            // given
            Exhibition exhibition = ExhibitionFixture.builder().id(1L).build();

            doReturn(Optional.of(exhibition)).when(exhibitionQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(List.of()).when(exhibitionTagQueryPort).findByExhibitionWithTag(any());
            doReturn(List.of()).when(exhibitionWorkQueryPort).findByExhibition(any());
            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(false).when(followQueryPort).existsByFollowerIdAndFolloweeId(any(), any());
            doReturn(false).when(exhibitionLikeQueryPort).existsByUserIdAndExhibitionId(any(), any());
            doReturn(false).when(exhibitionBookmarkQueryPort).existsByUserIdAndExhibitionId(any(), any());

            // when
            ExhibitionDetailsResult result = exhibitionQueryService.getExhibitionDetails(exhibition.getId());

            // then
            verify(exhibitionQueryPort).findByIdAndDeletedAtIsNull(exhibition.getId());
            verify(exhibitionTagQueryPort).findByExhibitionWithTag(any());
            verify(exhibitionWorkQueryPort).findByExhibition(any());
            verify(followQueryPort).existsByFollowerIdAndFolloweeId(any(), any());
            verify(exhibitionLikeQueryPort).existsByUserIdAndExhibitionId(any(), any());
            verify(exhibitionBookmarkQueryPort).existsByUserIdAndExhibitionId(any(), any());
            verify(exhibitionViewCountPort).incrementViewCount(exhibition.getId());
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("전시회가 존재하지 않으면 ExhibitionNotFoundException을 던진다")
        public void whenExhibitionNotFound() {
            // given
            doReturn(Optional.empty()).when(exhibitionQueryPort).findByIdAndDeletedAtIsNull(any());

            // when & then
            assertThrows(
                    ExhibitionNotFoundException.class,
                    () -> exhibitionQueryService.getExhibitionDetails(1L)
            );
            verify(exhibitionTagQueryPort, never()).findByExhibitionWithTag(any());
        }

        @Test
        @DisplayName("태그 조회에 실패하면 예외를 던진다")
        public void whenTagQueryFails() {
            // given
            Exhibition exhibition = ExhibitionFixture.builder().id(1L).build();

            doReturn(Optional.of(exhibition)).when(exhibitionQueryPort).findByIdAndDeletedAtIsNull(any());
            doThrow(new RuntimeException()).when(exhibitionTagQueryPort).findByExhibitionWithTag(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> exhibitionQueryService.getExhibitionDetails(exhibition.getId())
            );
            verify(exhibitionWorkQueryPort, never()).findByExhibition(any());
        }

        @Test
        @DisplayName("작품 조회에 실패하면 예외를 던진다")
        public void whenWorkQueryFails() {
            // given
            Exhibition exhibition = ExhibitionFixture.builder().id(1L).build();

            doReturn(Optional.of(exhibition)).when(exhibitionQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(List.of()).when(exhibitionTagQueryPort).findByExhibitionWithTag(any());
            doThrow(new RuntimeException()).when(exhibitionWorkQueryPort).findByExhibition(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> exhibitionQueryService.getExhibitionDetails(exhibition.getId())
            );
            verify(followQueryPort, never()).existsByFollowerIdAndFolloweeId(any(), any());
        }

        @Test
        @DisplayName("팔로우 조회에 실패하면 예외를 던진다")
        public void whenFollowQueryFails() {
            // given
            Exhibition exhibition = ExhibitionFixture.builder().id(1L).build();

            doReturn(Optional.of(exhibition)).when(exhibitionQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(List.of()).when(exhibitionTagQueryPort).findByExhibitionWithTag(any());
            doReturn(List.of()).when(exhibitionWorkQueryPort).findByExhibition(any());
            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doThrow(new RuntimeException()).when(followQueryPort).existsByFollowerIdAndFolloweeId(any(), any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> exhibitionQueryService.getExhibitionDetails(exhibition.getId())
            );
            verify(exhibitionLikeQueryPort, never()).existsByUserIdAndExhibitionId(any(), any());
        }

        @Test
        @DisplayName("좋아요 조회에 실패하면 예외를 던진다")
        public void whenLikeQueryFails() {
            // given
            Exhibition exhibition = ExhibitionFixture.builder().id(1L).build();

            doReturn(Optional.of(exhibition)).when(exhibitionQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(List.of()).when(exhibitionTagQueryPort).findByExhibitionWithTag(any());
            doReturn(List.of()).when(exhibitionWorkQueryPort).findByExhibition(any());
            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(false).when(followQueryPort).existsByFollowerIdAndFolloweeId(any(), any());
            doThrow(new RuntimeException()).when(exhibitionLikeQueryPort).existsByUserIdAndExhibitionId(any(), any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> exhibitionQueryService.getExhibitionDetails(exhibition.getId())
            );
            verify(exhibitionBookmarkQueryPort, never()).existsByUserIdAndExhibitionId(any(), any());
        }

        @Test
        @DisplayName("북마크 조회에 실패하면 예외를 던진다")
        public void whenBookmarkQueryFails() {
            // given
            Exhibition exhibition = ExhibitionFixture.builder().id(1L).build();

            doReturn(Optional.of(exhibition)).when(exhibitionQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(List.of()).when(exhibitionTagQueryPort).findByExhibitionWithTag(any());
            doReturn(List.of()).when(exhibitionWorkQueryPort).findByExhibition(any());
            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(false).when(followQueryPort).existsByFollowerIdAndFolloweeId(any(), any());
            doReturn(false).when(exhibitionLikeQueryPort).existsByUserIdAndExhibitionId(any(), any());
            doThrow(new RuntimeException()).when(exhibitionBookmarkQueryPort).existsByUserIdAndExhibitionId(any(), any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> exhibitionQueryService.getExhibitionDetails(exhibition.getId())
            );
            verify(exhibitionViewCountPort, never()).incrementViewCount(any());
        }

        @Test
        @DisplayName("조회수 증가에 실패하면 fallback으로 exhibitionCommandPort를 호출한다")
        public void whenViewCountFails() {
            // given
            Exhibition exhibition = ExhibitionFixture.builder().id(1L).build();

            doReturn(Optional.of(exhibition)).when(exhibitionQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(List.of()).when(exhibitionTagQueryPort).findByExhibitionWithTag(any());
            doReturn(List.of()).when(exhibitionWorkQueryPort).findByExhibition(any());
            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(false).when(followQueryPort).existsByFollowerIdAndFolloweeId(any(), any());
            doReturn(false).when(exhibitionLikeQueryPort).existsByUserIdAndExhibitionId(any(), any());
            doReturn(false).when(exhibitionBookmarkQueryPort).existsByUserIdAndExhibitionId(any(), any());
            doThrow(new DataAccessException("조회수 증가 실패") {
            }).when(exhibitionViewCountPort).incrementViewCount(any());

            // when
            exhibitionQueryService.getExhibitionDetails(exhibition.getId());

            // then
            verify(exhibitionCommandPort).incrementViewCount(exhibition.getId());
        }

        @Test
        @DisplayName("조회수 증가 fallback에도 실패하면 예외를 던진다")
        public void whenViewCountFallbackFails() {
            // given
            Exhibition exhibition = ExhibitionFixture.builder().id(1L).build();

            doReturn(Optional.of(exhibition)).when(exhibitionQueryPort).findByIdAndDeletedAtIsNull(any());
            doReturn(List.of()).when(exhibitionTagQueryPort).findByExhibitionWithTag(any());
            doReturn(List.of()).when(exhibitionWorkQueryPort).findByExhibition(any());
            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(false).when(followQueryPort).existsByFollowerIdAndFolloweeId(any(), any());
            doReturn(false).when(exhibitionLikeQueryPort).existsByUserIdAndExhibitionId(any(), any());
            doReturn(false).when(exhibitionBookmarkQueryPort).existsByUserIdAndExhibitionId(any(), any());
            doThrow(new DataAccessException("조회수 증가 실패") {
            }).when(exhibitionViewCountPort).incrementViewCount(any());
            doThrow(new RuntimeException()).when(exhibitionCommandPort).incrementViewCount(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> exhibitionQueryService.getExhibitionDetails(exhibition.getId())
            );
        }
    }

    @Nested
    @DisplayName("전시회 검색")
    class SearchExhibitionTest {
        @Test
        @DisplayName("인증된 유저이면 좋아요/북마크 정보가 포함된 검색 결과를 반환한다")
        public void whenAuthenticated() {
            // given
            ExhibitionSearchQuery query = ExhibitionSearchQueryFixture.builder().build();

            doReturn(Page.empty()).when(exhibitionQueryPort).searchExhibition(any(), any(), any());
            doReturn(true).when(authenticationUserProviderPort).isAuthenticated();
            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Set.of()).when(exhibitionLikeQueryPort).findExhibitionIds(any(), any());
            doReturn(Set.of()).when(exhibitionBookmarkQueryPort).findExhibitionIds(any(), any());

            // when
            ExhibitionSearchResult result = exhibitionQueryService.searchExhibition(query);

            // then
            verify(exhibitionQueryPort).searchExhibition(any(), any(), any());
            verify(exhibitionLikeQueryPort).findExhibitionIds(any(), any());
            verify(exhibitionBookmarkQueryPort).findExhibitionIds(any(), any());
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("인증되지 않은 유저이면 좋아요/북마크 조회 없이 검색 결과를 반환한다")
        public void whenNotAuthenticated() {
            // given
            ExhibitionSearchQuery query = ExhibitionSearchQueryFixture.builder().build();

            doReturn(Page.empty()).when(exhibitionQueryPort).searchExhibition(any(), any(), any());
            doReturn(false).when(authenticationUserProviderPort).isAuthenticated();

            // when
            ExhibitionSearchResult result = exhibitionQueryService.searchExhibition(query);

            // then
            verify(exhibitionQueryPort).searchExhibition(any(), any(), any());
            verify(exhibitionLikeQueryPort, never()).findExhibitionIds(any(), any());
            verify(exhibitionBookmarkQueryPort, never()).findExhibitionIds(any(), any());
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("전시회 검색에 실패하면 예외를 던진다")
        public void whenExhibitionSearchFails() {
            // given
            ExhibitionSearchQuery query = ExhibitionSearchQueryFixture.builder().build();

            doThrow(new RuntimeException()).when(exhibitionQueryPort).searchExhibition(any(), any(), any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> exhibitionQueryService.searchExhibition(query)
            );
            verify(exhibitionLikeQueryPort, never()).findExhibitionIds(any(), any());
        }

        @Test
        @DisplayName("좋아요 조회에 실패하면 예외를 던진다")
        public void whenLikeQueryFails() {
            // given
            ExhibitionSearchQuery query = ExhibitionSearchQueryFixture.builder().build();

            doReturn(Page.empty()).when(exhibitionQueryPort).searchExhibition(any(), any(), any());
            doReturn(true).when(authenticationUserProviderPort).isAuthenticated();
            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doThrow(new RuntimeException()).when(exhibitionLikeQueryPort).findExhibitionIds(any(), any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> exhibitionQueryService.searchExhibition(query)
            );
            verify(exhibitionBookmarkQueryPort, never()).findExhibitionIds(any(), any());
        }

        @Test
        @DisplayName("북마크 조회에 실패하면 예외를 던진다")
        public void whenBookmarkQueryFails() {
            // given
            ExhibitionSearchQuery query = ExhibitionSearchQueryFixture.builder().build();

            doReturn(Page.empty()).when(exhibitionQueryPort).searchExhibition(any(), any(), any());
            doReturn(true).when(authenticationUserProviderPort).isAuthenticated();
            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Set.of()).when(exhibitionLikeQueryPort).findExhibitionIds(any(), any());
            doThrow(new RuntimeException()).when(exhibitionBookmarkQueryPort).findExhibitionIds(any(), any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> exhibitionQueryService.searchExhibition(query)
            );
        }
    }

    @Nested
    @DisplayName("내 전시회 검색")
    class SearchMyExhibitionTest {
        @Test
        @DisplayName("인증된 유저이면 좋아요/북마크 정보가 포함된 검색 결과를 반환한다")
        public void whenAuthenticated() {
            // given
            MyExhibitionSearchQuery query = MyExhibitionSearchQueryFixture.builder().build();

            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Page.empty()).when(exhibitionQueryPort).searchMyExhibitionByDeletedAtIsNull(any(), any(), any());
            doReturn(true).when(authenticationUserProviderPort).isAuthenticated();
            doReturn(Set.of()).when(exhibitionLikeQueryPort).findExhibitionIds(any(), any());
            doReturn(Set.of()).when(exhibitionBookmarkQueryPort).findExhibitionIds(any(), any());

            // when
            MyExhibitionSearchResult result = exhibitionQueryService.searchMyExhibition(query);

            // then
            verify(authenticationUserProviderPort, Mockito.times(3)).getCurrentUserId();
            verify(exhibitionQueryPort).searchMyExhibitionByDeletedAtIsNull(any(), any(), any());
            verify(exhibitionLikeQueryPort).findExhibitionIds(any(), any());
            verify(exhibitionBookmarkQueryPort).findExhibitionIds(any(), any());
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("인증되지 않은 유저이면 좋아요/북마크 조회 없이 검색 결과를 반환한다")
        public void whenNotAuthenticated() {
            // given
            MyExhibitionSearchQuery query = MyExhibitionSearchQueryFixture.builder().build();

            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Page.empty()).when(exhibitionQueryPort).searchMyExhibitionByDeletedAtIsNull(any(), any(), any());
            doReturn(false).when(authenticationUserProviderPort).isAuthenticated();

            // when
            MyExhibitionSearchResult result = exhibitionQueryService.searchMyExhibition(query);

            // then
            verify(exhibitionQueryPort).searchMyExhibitionByDeletedAtIsNull(any(), any(), any());
            verify(exhibitionLikeQueryPort, never()).findExhibitionIds(any(), any());
            verify(exhibitionBookmarkQueryPort, never()).findExhibitionIds(any(), any());
            assertThat(result).isNotNull();
        }
    }
}
