package com.benchpress200.photique.exhibition.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.benchpress200.photique.exhibition.application.query.model.ExhibitionCommentsQuery;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionCommentQueryPort;
import com.benchpress200.photique.exhibition.application.query.result.ExhibitionCommentsResult;
import com.benchpress200.photique.exhibition.application.query.service.ExhibitionCommentQueryService;
import com.benchpress200.photique.exhibition.application.query.support.fixture.ExhibitionCommentsQueryFixture;
import com.benchpress200.photique.support.base.BaseServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;

@DisplayName("전시회 감상평 쿼리 서비스 테스트")
public class ExhibitionCommentQueryServiceTest extends BaseServiceTest {
    @InjectMocks
    private ExhibitionCommentQueryService exhibitionCommentQueryService;

    @Mock
    private ExhibitionCommentQueryPort exhibitionCommentQueryPort;

    @Nested
    @DisplayName("전시회 감상평 페이지 조회")
    class GetExhibitionCommentsTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenQueryValid() {
            // given
            ExhibitionCommentsQuery query = ExhibitionCommentsQueryFixture.builder().build();

            doReturn(Page.empty()).when(exhibitionCommentQueryPort).findByExhibitionId(any(), any());

            // when
            ExhibitionCommentsResult result = exhibitionCommentQueryService.getExhibitionComments(query);

            // then
            verify(exhibitionCommentQueryPort).findByExhibitionId(query.getExhibitionId(), query.getPageable());
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("감상평 조회에 실패하면 예외를 던진다")
        public void whenCommentQueryFails() {
            // given
            ExhibitionCommentsQuery query = ExhibitionCommentsQueryFixture.builder().build();

            doThrow(new RuntimeException()).when(exhibitionCommentQueryPort).findByExhibitionId(any(), any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> exhibitionCommentQueryService.getExhibitionComments(query)
            );
        }
    }
}
