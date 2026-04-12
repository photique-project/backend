package com.benchpress200.photique.exhibition.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
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
import com.benchpress200.photique.exhibition.application.query.service.ExhibitionQueryService;
import com.benchpress200.photique.singlework.application.query.model.MyExhibitionSearchQuery;
import com.benchpress200.photique.singlework.application.query.result.MyExhibitionSearchResult;
import com.benchpress200.photique.singlework.application.query.support.fixture.MyExhibitionSearchQueryFixture;
import com.benchpress200.photique.support.base.BaseServiceTest;
import com.benchpress200.photique.user.application.query.port.out.persistence.FollowQueryPort;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
