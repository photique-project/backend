package com.benchpress200.photique.singlework.application.query;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.benchpress200.photique.singlework.application.query.model.SingleWorkCommentsQuery;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkCommentQueryPort;
import com.benchpress200.photique.singlework.application.query.result.SingleWorkCommentsResult;
import com.benchpress200.photique.singlework.application.query.service.SingleWorkCommentQueryService;
import com.benchpress200.photique.singlework.application.query.support.fixture.SingleWorkCommentsQueryFixture;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
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

@DisplayName("단일작품 댓글 쿼리 서비스 테스트")
public class SingleWorkCommentQueryServiceTest extends BaseServiceTest {
    @InjectMocks
    private SingleWorkCommentQueryService singleWorkCommentQueryService;

    @Mock
    private SingleWorkCommentQueryPort singleWorkCommentQueryPort;

    @Nested
    @DisplayName("단일작품 댓글 페이지 조회")
    class GetSingleWorkCommentsTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenQueryValid() {
            // given
            SingleWorkCommentsQuery query = SingleWorkCommentsQueryFixture.builder().build();
            Page<SingleWorkComment> commentPage = new PageImpl<>(List.of(), PageRequest.of(0, 30), 0);

            doReturn(commentPage).when(singleWorkCommentQueryPort).findBySingleWorkIdAndDeletedAtIsNull(any(), any());

            // when
            SingleWorkCommentsResult result = singleWorkCommentQueryService.getSingleWorkComments(query);

            // then
            verify(singleWorkCommentQueryPort).findBySingleWorkIdAndDeletedAtIsNull(query.getSingleWorkId(), query.getPageable());
            assertNotNull(result);
        }

        @Test
        @DisplayName("댓글 페이지 조회에 실패하면 예외를 던진다")
        public void whenFindPageFails() {
            // given
            SingleWorkCommentsQuery query = SingleWorkCommentsQueryFixture.builder().build();

            doThrow(new RuntimeException()).when(singleWorkCommentQueryPort).findBySingleWorkIdAndDeletedAtIsNull(any(), any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> singleWorkCommentQueryService.getSingleWorkComments(query)
            );
        }
    }
}
