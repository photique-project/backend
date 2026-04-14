package com.benchpress200.photique.exhibition.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.exhibition.application.query.model.LikedExhibitionSearchQuery;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionBookmarkQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionLikeQueryPort;
import com.benchpress200.photique.exhibition.application.query.result.LikedExhibitionSearchResult;
import com.benchpress200.photique.exhibition.application.query.service.ExhibitionLikeQueryService;
import com.benchpress200.photique.exhibition.application.query.support.fixture.LikedExhibitionSearchQueryFixture;
import com.benchpress200.photique.support.base.BaseServiceTest;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;

@DisplayName("전시회 좋아요 쿼리 서비스 테스트")
public class ExhibitionLikeQueryServiceTest extends BaseServiceTest {
    @InjectMocks
    private ExhibitionLikeQueryService exhibitionLikeQueryService;

    @Mock
    private AuthenticationUserProviderPort authenticationUserProviderPort;

    @Mock
    private ExhibitionBookmarkQueryPort exhibitionBookmarkQueryPort;

    @Mock
    private ExhibitionLikeQueryPort exhibitionLikeQueryPort;

    @Nested
    @DisplayName("좋아요한 전시회 검색")
    class SearchLikedExhibitionTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenQueryValid() {
            // given
            LikedExhibitionSearchQuery query = LikedExhibitionSearchQueryFixture.builder().build();

            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Page.empty()).when(exhibitionLikeQueryPort).searchLikedExhibitionByDeletedAtIsNull(any(), any(), any());
            doReturn(Set.of()).when(exhibitionBookmarkQueryPort).findExhibitionIds(any(), any());

            // when
            LikedExhibitionSearchResult result = exhibitionLikeQueryService.searchLikedExhibition(query);

            // then
            verify(exhibitionLikeQueryPort).searchLikedExhibitionByDeletedAtIsNull(any(), any(), any());
            verify(exhibitionBookmarkQueryPort).findExhibitionIds(any(), any());
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("좋아요 검색에 실패하면 예외를 던진다")
        public void whenLikeSearchFails() {
            // given
            LikedExhibitionSearchQuery query = LikedExhibitionSearchQueryFixture.builder().build();

            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doThrow(new RuntimeException()).when(exhibitionLikeQueryPort).searchLikedExhibitionByDeletedAtIsNull(any(), any(), any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> exhibitionLikeQueryService.searchLikedExhibition(query)
            );
            verify(exhibitionBookmarkQueryPort, never()).findExhibitionIds(any(), any());
        }

        @Test
        @DisplayName("북마크 조회에 실패하면 예외를 던진다")
        public void whenBookmarkQueryFails() {
            // given
            LikedExhibitionSearchQuery query = LikedExhibitionSearchQueryFixture.builder().build();

            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Page.empty()).when(exhibitionLikeQueryPort).searchLikedExhibitionByDeletedAtIsNull(any(), any(), any());
            doThrow(new RuntimeException()).when(exhibitionBookmarkQueryPort).findExhibitionIds(any(), any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> exhibitionLikeQueryService.searchLikedExhibition(query)
            );
        }
    }
}
