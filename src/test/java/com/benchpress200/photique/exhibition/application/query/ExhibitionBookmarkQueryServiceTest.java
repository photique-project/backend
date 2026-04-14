package com.benchpress200.photique.exhibition.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.exhibition.application.query.model.BookmarkedExhibitionSearchQuery;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionBookmarkQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionLikeQueryPort;
import com.benchpress200.photique.exhibition.application.query.result.BookmarkedExhibitionSearchResult;
import com.benchpress200.photique.exhibition.application.query.service.ExhibitionBookmarkQueryService;
import com.benchpress200.photique.exhibition.application.query.support.fixture.BookmarkedExhibitionSearchQueryFixture;
import com.benchpress200.photique.support.base.BaseServiceTest;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;

@DisplayName("전시회 북마크 쿼리 서비스 테스트")
public class ExhibitionBookmarkQueryServiceTest extends BaseServiceTest {
    @InjectMocks
    private ExhibitionBookmarkQueryService exhibitionBookmarkQueryService;

    @Mock
    private AuthenticationUserProviderPort authenticationUserProviderPort;

    @Mock
    private ExhibitionBookmarkQueryPort exhibitionBookmarkQueryPort;

    @Mock
    private ExhibitionLikeQueryPort exhibitionLikeQueryPort;

    @Nested
    @DisplayName("북마크한 전시회 검색")
    class SearchBookmarkedExhibitionTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenQueryValid() {
            // given
            BookmarkedExhibitionSearchQuery query = BookmarkedExhibitionSearchQueryFixture.builder().build();

            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Page.empty()).when(exhibitionBookmarkQueryPort).searchBookmarkedExhibitionByDeletedAtIsNull(any(), any(), any());
            doReturn(Set.of()).when(exhibitionLikeQueryPort).findExhibitionIds(any(), any());

            // when
            BookmarkedExhibitionSearchResult result = exhibitionBookmarkQueryService.searchBookmarkedExhibition(query);

            // then
            verify(exhibitionBookmarkQueryPort).searchBookmarkedExhibitionByDeletedAtIsNull(any(), any(), any());
            verify(exhibitionLikeQueryPort).findExhibitionIds(any(), any());
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("북마크 검색에 실패하면 예외를 던진다")
        public void whenBookmarkSearchFails() {
            // given
            BookmarkedExhibitionSearchQuery query = BookmarkedExhibitionSearchQueryFixture.builder().build();

            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doThrow(new RuntimeException()).when(exhibitionBookmarkQueryPort).searchBookmarkedExhibitionByDeletedAtIsNull(any(), any(), any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> exhibitionBookmarkQueryService.searchBookmarkedExhibition(query)
            );
            verify(exhibitionLikeQueryPort, never()).findExhibitionIds(any(), any());
        }

        @Test
        @DisplayName("좋아요 조회에 실패하면 예외를 던진다")
        public void whenLikeQueryFails() {
            // given
            BookmarkedExhibitionSearchQuery query = BookmarkedExhibitionSearchQueryFixture.builder().build();

            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(Page.empty()).when(exhibitionBookmarkQueryPort).searchBookmarkedExhibitionByDeletedAtIsNull(any(), any(), any());
            doThrow(new RuntimeException()).when(exhibitionLikeQueryPort).findExhibitionIds(any(), any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> exhibitionBookmarkQueryService.searchBookmarkedExhibition(query)
            );
        }
    }
}
