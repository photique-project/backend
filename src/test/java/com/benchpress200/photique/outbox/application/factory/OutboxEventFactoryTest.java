package com.benchpress200.photique.outbox.application.factory;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import com.benchpress200.photique.outbox.domain.entity.OutboxEvent;
import com.benchpress200.photique.outbox.domain.enumeration.AggregateType;
import com.benchpress200.photique.outbox.domain.enumeration.EventType;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.enumeration.Provider;
import com.benchpress200.photique.user.domain.enumeration.Role;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("OutboxEventFactory 테스트")
public class OutboxEventFactoryTest {

    private OutboxEventFactory outboxEventFactory;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        outboxEventFactory = new OutboxEventFactory(objectMapper);
    }

    @Test
    @DisplayName("singleWorkCreated - 단일작품 생성 이벤트 생성")
    void singleWorkCreated_단일작품_생성_이벤트_생성() {
        // GIVEN
        User writer = createUser(1L, "testuser");
        SingleWork singleWork = createSingleWork(100L, writer, "Test Title", Category.LANDSCAPE);
        List<String> tagNames = Arrays.asList("nature", "landscape");

        // WHEN
        OutboxEvent event = outboxEventFactory.singleWorkCreated(singleWork, tagNames);

        // THEN
        Assertions.assertThat(event).isNotNull();
        Assertions.assertThat(event.getAggregateType()).isEqualTo(AggregateType.SINGLEWORK);
        Assertions.assertThat(event.getAggregateId()).isEqualTo("100");
        Assertions.assertThat(event.getEventType()).isEqualTo(EventType.CREATED);
        Assertions.assertThat(event.getPayload()).isNotNull();
        Assertions.assertThat(event.getPayload().get("id").asLong()).isEqualTo(100L);
        Assertions.assertThat(event.getPayload().get("title").asText()).isEqualTo("Test Title");
    }

    @Test
    @DisplayName("singleWorkUpdated - 단일작품 수정 이벤트 생성")
    void singleWorkUpdated_단일작품_수정_이벤트_생성() {
        // GIVEN
        User writer = createUser(1L, "testuser");
        SingleWork singleWork = createSingleWork(200L, writer, "Updated Title", Category.PORTRAIT);
        List<String> tagNames = Arrays.asList("portrait", "updated");

        // WHEN
        OutboxEvent event = outboxEventFactory.singleWorkUpdated(singleWork, tagNames);

        // THEN
        Assertions.assertThat(event).isNotNull();
        Assertions.assertThat(event.getAggregateType()).isEqualTo(AggregateType.SINGLEWORK);
        Assertions.assertThat(event.getAggregateId()).isEqualTo("200");
        Assertions.assertThat(event.getEventType()).isEqualTo(EventType.UPDATED);
        Assertions.assertThat(event.getPayload()).isNotNull();
    }

    @Test
    @DisplayName("singleWorkLiked - 단일작품 좋아요 이벤트 생성")
    void singleWorkLiked_단일작품_좋아요_이벤트_생성() {
        // GIVEN
        User writer = createUser(1L, "testuser");
        SingleWork singleWork = createSingleWork(300L, writer, "Liked Work", Category.STREET);

        // WHEN
        OutboxEvent event = outboxEventFactory.singleWorkLiked(singleWork);

        // THEN
        Assertions.assertThat(event).isNotNull();
        Assertions.assertThat(event.getAggregateType()).isEqualTo(AggregateType.SINGLEWORK);
        Assertions.assertThat(event.getAggregateId()).isEqualTo("300");
        Assertions.assertThat(event.getEventType()).isEqualTo(EventType.LIKED);
        Assertions.assertThat(event.getPayload()).isNotNull();
        Assertions.assertThat(event.getPayload().get("id").asLong()).isEqualTo(300L);
    }

    @Test
    @DisplayName("singleWorkUnliked - 단일작품 좋아요 취소 이벤트 생성")
    void singleWorkUnliked_단일작품_좋아요_취소_이벤트_생성() {
        // GIVEN
        User writer = createUser(1L, "testuser");
        SingleWork singleWork = createSingleWork(400L, writer, "Unliked Work", Category.ANIMAL);

        // WHEN
        OutboxEvent event = outboxEventFactory.singleWorkUnliked(singleWork);

        // THEN
        Assertions.assertThat(event).isNotNull();
        Assertions.assertThat(event.getAggregateType()).isEqualTo(AggregateType.SINGLEWORK);
        Assertions.assertThat(event.getAggregateId()).isEqualTo("400");
        Assertions.assertThat(event.getEventType()).isEqualTo(EventType.UNLIKED);
        Assertions.assertThat(event.getPayload()).isNotNull();
    }

    @Test
    @DisplayName("singleWorkDeleted - 단일작품 삭제 이벤트 생성")
    void singleWorkDeleted_단일작품_삭제_이벤트_생성() {
        // GIVEN
        User writer = createUser(1L, "testuser");
        SingleWork singleWork = createSingleWork(500L, writer, "Deleted Work", Category.LANDSCAPE);

        // WHEN
        OutboxEvent event = outboxEventFactory.singleWorkDeleted(singleWork);

        // THEN
        Assertions.assertThat(event).isNotNull();
        Assertions.assertThat(event.getAggregateType()).isEqualTo(AggregateType.SINGLEWORK);
        Assertions.assertThat(event.getAggregateId()).isEqualTo("500");
        Assertions.assertThat(event.getEventType()).isEqualTo(EventType.DELETED);
        Assertions.assertThat(event.getPayload()).isNotNull();
        Assertions.assertThat(event.getPayload().get("id").asLong()).isEqualTo(500L);
    }

    @Test
    @DisplayName("singleWorkCommentCreated - 단일작품 댓글 생성 이벤트 생성")
    void singleWorkCommentCreated_단일작품_댓글_생성_이벤트_생성() {
        // GIVEN
        User writer = createUser(1L, "testuser");
        SingleWork singleWork = createSingleWork(100L, writer, "Test Title", Category.LANDSCAPE);
        SingleWorkComment comment = createSingleWorkComment(10L, writer, singleWork, "Test Comment");

        // WHEN
        OutboxEvent event = outboxEventFactory.singleWorkCommentCreated(comment);

        // THEN
        Assertions.assertThat(event).isNotNull();
        Assertions.assertThat(event.getAggregateType()).isEqualTo(AggregateType.SINGLEWORK_COMMENT);
        Assertions.assertThat(event.getAggregateId()).isEqualTo("10");
        Assertions.assertThat(event.getEventType()).isEqualTo(EventType.CREATED);
        Assertions.assertThat(event.getPayload()).isNotNull();
    }

    @Test
    @DisplayName("exhibitionCreated - 전시회 생성 이벤트 생성")
    void exhibitionCreated_전시회_생성_이벤트_생성() {
        // GIVEN
        User writer = createUser(1L, "testuser");
        Exhibition exhibition = createExhibition(100L, writer, "Test Exhibition", "#FF5733");
        List<String> tagNames = Arrays.asList("art", "photography");

        // WHEN
        OutboxEvent event = outboxEventFactory.exhibitionCreated(exhibition, tagNames);

        // THEN
        Assertions.assertThat(event).isNotNull();
        Assertions.assertThat(event.getAggregateType()).isEqualTo(AggregateType.EXHIBITION);
        Assertions.assertThat(event.getAggregateId()).isEqualTo("100");
        Assertions.assertThat(event.getEventType()).isEqualTo(EventType.CREATED);
        Assertions.assertThat(event.getPayload()).isNotNull();
        Assertions.assertThat(event.getPayload().get("id").asLong()).isEqualTo(100L);
        Assertions.assertThat(event.getPayload().get("title").asText()).isEqualTo("Test Exhibition");
    }

    @Test
    @DisplayName("exhibitionUpdated - 전시회 수정 이벤트 생성")
    void exhibitionUpdated_전시회_수정_이벤트_생성() {
        // GIVEN
        User writer = createUser(1L, "testuser");
        Exhibition exhibition = createExhibition(200L, writer, "Updated Exhibition", "#00FF00");
        List<String> tagNames = Arrays.asList("updated");

        // WHEN
        OutboxEvent event = outboxEventFactory.exhibitionUpdated(exhibition, tagNames);

        // THEN
        Assertions.assertThat(event).isNotNull();
        Assertions.assertThat(event.getAggregateType()).isEqualTo(AggregateType.EXHIBITION);
        Assertions.assertThat(event.getAggregateId()).isEqualTo("200");
        Assertions.assertThat(event.getEventType()).isEqualTo(EventType.UPDATED);
        Assertions.assertThat(event.getPayload()).isNotNull();
    }

    @Test
    @DisplayName("exhibitionLiked - 전시회 좋아요 이벤트 생성")
    void exhibitionLiked_전시회_좋아요_이벤트_생성() {
        // GIVEN
        User writer = createUser(1L, "testuser");
        Exhibition exhibition = createExhibition(300L, writer, "Liked Exhibition", "#0000FF");

        // WHEN
        OutboxEvent event = outboxEventFactory.exhibitionLiked(exhibition);

        // THEN
        Assertions.assertThat(event).isNotNull();
        Assertions.assertThat(event.getAggregateType()).isEqualTo(AggregateType.EXHIBITION);
        Assertions.assertThat(event.getAggregateId()).isEqualTo("300");
        Assertions.assertThat(event.getEventType()).isEqualTo(EventType.LIKED);
        Assertions.assertThat(event.getPayload()).isNotNull();
    }

    @Test
    @DisplayName("exhibitionUnliked - 전시회 좋아요 취소 이벤트 생성")
    void exhibitionUnliked_전시회_좋아요_취소_이벤트_생성() {
        // GIVEN
        User writer = createUser(1L, "testuser");
        Exhibition exhibition = createExhibition(400L, writer, "Unliked Exhibition", "#FF00FF");

        // WHEN
        OutboxEvent event = outboxEventFactory.exhibitionUnliked(exhibition);

        // THEN
        Assertions.assertThat(event).isNotNull();
        Assertions.assertThat(event.getAggregateType()).isEqualTo(AggregateType.EXHIBITION);
        Assertions.assertThat(event.getAggregateId()).isEqualTo("400");
        Assertions.assertThat(event.getEventType()).isEqualTo(EventType.UNLIKED);
        Assertions.assertThat(event.getPayload()).isNotNull();
    }

    @Test
    @DisplayName("exhibitionDeleted - 전시회 삭제 이벤트 생성")
    void exhibitionDeleted_전시회_삭제_이벤트_생성() {
        // GIVEN
        User writer = createUser(1L, "testuser");
        Exhibition exhibition = createExhibition(500L, writer, "Deleted Exhibition", "#000000");

        // WHEN
        OutboxEvent event = outboxEventFactory.exhibitionDeleted(exhibition);

        // THEN
        Assertions.assertThat(event).isNotNull();
        Assertions.assertThat(event.getAggregateType()).isEqualTo(AggregateType.EXHIBITION);
        Assertions.assertThat(event.getAggregateId()).isEqualTo("500");
        Assertions.assertThat(event.getEventType()).isEqualTo(EventType.DELETED);
        Assertions.assertThat(event.getPayload()).isNotNull();
        Assertions.assertThat(event.getPayload().get("id").asLong()).isEqualTo(500L);
    }

    @Test
    @DisplayName("exhibitionCommentCreated - 전시회 댓글 생성 이벤트 생성")
    void exhibitionCommentCreated_전시회_댓글_생성_이벤트_생성() {
        // GIVEN
        User writer = createUser(1L, "testuser");
        Exhibition exhibition = createExhibition(100L, writer, "Test Exhibition", "#FF5733");
        ExhibitionComment comment = createExhibitionComment(20L, writer, exhibition, "Test Comment");

        // WHEN
        OutboxEvent event = outboxEventFactory.exhibitionCommentCreated(comment);

        // THEN
        Assertions.assertThat(event).isNotNull();
        Assertions.assertThat(event.getAggregateType()).isEqualTo(AggregateType.EXHIBITION_COMMENT);
        Assertions.assertThat(event.getAggregateId()).isEqualTo("20");
        Assertions.assertThat(event.getEventType()).isEqualTo(EventType.CREATED);
        Assertions.assertThat(event.getPayload()).isNotNull();
    }

    @Test
    @DisplayName("userUpdated - 사용자 정보 수정 이벤트 생성")
    void userUpdated_사용자_정보_수정_이벤트_생성() {
        // GIVEN
        User user = createUser(1L, "testuser");

        // WHEN
        OutboxEvent event = outboxEventFactory.userUpdated(user);

        // THEN
        Assertions.assertThat(event).isNotNull();
        Assertions.assertThat(event.getAggregateType()).isEqualTo(AggregateType.USER);
        Assertions.assertThat(event.getAggregateId()).isEqualTo("1");
        Assertions.assertThat(event.getEventType()).isEqualTo(EventType.UPDATED);
        Assertions.assertThat(event.getPayload()).isNotNull();
    }

    @Test
    @DisplayName("follow - 팔로우 이벤트 생성")
    void follow_팔로우_이벤트_생성() {
        // GIVEN
        User follower = createUser(1L, "follower");
        User followee = createUser(2L, "followee");
        Follow follow = new Follow(follower, followee);

        // WHEN
        OutboxEvent event = outboxEventFactory.follow(follow);

        // THEN
        Assertions.assertThat(event).isNotNull();
        Assertions.assertThat(event.getAggregateType()).isEqualTo(AggregateType.FOLLOW);
        Assertions.assertThat(event.getAggregateId()).isEqualTo("1"); // follower의 id 사용
        Assertions.assertThat(event.getEventType()).isEqualTo(EventType.CREATED);
        Assertions.assertThat(event.getPayload()).isNotNull();
    }

    @Test
    @DisplayName("singleWorkCreated - 빈 태그 리스트로 이벤트 생성")
    void singleWorkCreated_빈_태그_리스트로_이벤트_생성() {
        // GIVEN
        User writer = createUser(1L, "testuser");
        SingleWork singleWork = createSingleWork(600L, writer, "No Tags Work", Category.LANDSCAPE);
        List<String> emptyTags = Arrays.asList();

        // WHEN
        OutboxEvent event = outboxEventFactory.singleWorkCreated(singleWork, emptyTags);

        // THEN
        Assertions.assertThat(event).isNotNull();
        Assertions.assertThat(event.getAggregateType()).isEqualTo(AggregateType.SINGLEWORK);
        Assertions.assertThat(event.getAggregateId()).isEqualTo("600");
        Assertions.assertThat(event.getEventType()).isEqualTo(EventType.CREATED);
        Assertions.assertThat(event.getPayload()).isNotNull();
    }

    @Test
    @DisplayName("payload가 JsonNode 타입으로 변환되는지 확인")
    void payload가_JsonNode_타입으로_변환되는지_확인() {
        // GIVEN
        User writer = createUser(1L, "testuser");
        SingleWork singleWork = createSingleWork(700L, writer, "Test Title", Category.LANDSCAPE);
        List<String> tagNames = Arrays.asList("test");

        // WHEN
        OutboxEvent event = outboxEventFactory.singleWorkCreated(singleWork, tagNames);

        // THEN
        Assertions.assertThat(event.getPayload()).isInstanceOf(JsonNode.class);
        Assertions.assertThat(event.getPayload().isObject()).isTrue();
    }

    // Helper methods
    private User createUser(Long id, String nickname) {
        User user = User.builder()
                .email(nickname + "@example.com")
                .password("password123")
                .nickname(nickname)
                .profileImage("https://example.com/" + nickname + ".jpg")
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .build();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private SingleWork createSingleWork(Long id, User writer, String title, Category category) {
        SingleWork singleWork = SingleWork.builder()
                .writer(writer)
                .title(title)
                .description("Test Description")
                .image("https://example.com/image.jpg")
                .camera("Canon EOS R5")
                .category(category)
                .date(LocalDate.now())
                .build();
        ReflectionTestUtils.setField(singleWork, "id", id);
        return singleWork;
    }

    private Exhibition createExhibition(Long id, User writer, String title, String cardColor) {
        Exhibition exhibition = Exhibition.builder()
                .writer(writer)
                .title(title)
                .description("Test Exhibition Description")
                .cardColor(cardColor)
                .build();
        ReflectionTestUtils.setField(exhibition, "id", id);
        return exhibition;
    }

    private SingleWorkComment createSingleWorkComment(Long id, User writer, SingleWork singleWork, String content) {
        SingleWorkComment comment = SingleWorkComment.builder()
                .writer(writer)
                .singleWork(singleWork)
                .content(content)
                .build();
        ReflectionTestUtils.setField(comment, "id", id);
        return comment;
    }

    private ExhibitionComment createExhibitionComment(Long id, User writer, Exhibition exhibition, String content) {
        ExhibitionComment comment = ExhibitionComment.builder()
                .exhibition(exhibition)
                .writer(writer)
                .content(content)
                .build();
        ReflectionTestUtils.setField(comment, "id", id);
        return comment;
    }
}