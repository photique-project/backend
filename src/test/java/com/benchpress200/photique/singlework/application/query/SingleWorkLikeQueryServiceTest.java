package com.benchpress200.photique.singlework.application.query;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.singlework.application.query.model.LikedSingleWorkSearchQuery;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkLikeQueryPort;
import com.benchpress200.photique.singlework.application.query.result.LikedSingleWorkSearchResult;
import com.benchpress200.photique.singlework.application.query.service.SingleWorkLikeQueryService;
import com.benchpress200.photique.singlework.application.query.support.fixture.LikedSingleWorkSearchQueryFixture;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkLike;
import com.benchpress200.photique.support.base.BaseServiceTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@DisplayName("단일작품 좋아요 쿼리 서비스 테스트")
public class SingleWorkLikeQueryServiceTest extends BaseServiceTest {
    @InjectMocks
    private SingleWorkLikeQueryService singleWorkLikeQueryService;

    @Mock
    private AuthenticationUserProviderPort authenticationUserProvider;

    @Mock
    private SingleWorkLikeQueryPort singleWorkLikeQueryPort;

    @Nested
    @DisplayName("좋아요한 단일작품 검색")
    class SearchLikedSingleWorkTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenQueryValid() {
            // given
            LikedSingleWorkSearchQuery query = LikedSingleWorkSearchQueryFixture.builder().build();
            Page<SingleWorkLike> singleWorkLikePage = new PageImpl<>(List.of(), PageRequest.of(0, 30), 0);

            doReturn(1L).when(authenticationUserProvider).getCurrentUserId();
            doReturn(singleWorkLikePage).when(singleWorkLikeQueryPort).searchLikedSingleWorkByDeletedAtIsNull(any(), any(), any());

            // when
            LikedSingleWorkSearchResult result = singleWorkLikeQueryService.searchLikedSingleWork(query);

            // then
            verify(authenticationUserProvider).getCurrentUserId();
            verify(singleWorkLikeQueryPort).searchLikedSingleWorkByDeletedAtIsNull(1L, query.getKeyword(), query.getPageable());
            assertNotNull(result);
        }

        @Test
        @DisplayName("좋아요한 단일작품 검색에 실패하면 예외를 던진다")
        public void whenSearchFails() {
            // given
            LikedSingleWorkSearchQuery query = LikedSingleWorkSearchQueryFixture.builder().build();

            doReturn(1L).when(authenticationUserProvider).getCurrentUserId();
            doThrow(new RuntimeException()).when(singleWorkLikeQueryPort).searchLikedSingleWorkByDeletedAtIsNull(any(), any(), any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> singleWorkLikeQueryService.searchLikedSingleWork(query)
            );
        }
    }
}
