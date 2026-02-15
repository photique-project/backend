package com.benchpress200.photique.outbox.application.payload;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.enumeration.Provider;
import com.benchpress200.photique.user.domain.enumeration.Role;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("ExhibitionPayload 테스트")
public class ExhibitionPayloadTest {

    @Test
    @DisplayName("of - Exhibition과 태그 리스트로 전체 정보를 가진 Payload 생성")
    void of_Exhibition과_태그_리스트로_전체_정보를_가진_Payload_생성() {
        // GIVEN
        User writer = User.builder()
                .email("test@example.com")
                .password("password123")
                .nickname("testuser")
                .profileImage("https://example.com/profile.jpg")
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .build();
        ReflectionTestUtils.setField(writer, "id", 1L);

        Exhibition exhibition = Exhibition.builder()
                .writer(writer)
                .title("Test Exhibition")
                .description("Test Exhibition Description")
                .cardColor("#FF5733")
                .build();
        ReflectionTestUtils.setField(exhibition, "id", 100L);
        ReflectionTestUtils.setField(exhibition, "viewCount", 50L);
        ReflectionTestUtils.setField(exhibition, "likeCount", 10L);

        List<String> tagNames = Arrays.asList("art", "photography", "exhibition");

        // WHEN
        ExhibitionPayload payload = ExhibitionPayload.of(exhibition, tagNames);

        // THEN
        Assertions.assertThat(payload).isNotNull();
        Assertions.assertThat(payload.getId()).isEqualTo(100L);
        Assertions.assertThat(payload.getTitle()).isEqualTo("Test Exhibition");
        Assertions.assertThat(payload.getDescription()).isEqualTo("Test Exhibition Description");
        Assertions.assertThat(payload.getCardColor()).isEqualTo("#FF5733");
        Assertions.assertThat(payload.getViewCount()).isEqualTo(50L);
        Assertions.assertThat(payload.getLikeCount()).isEqualTo(10L);
        Assertions.assertThat(payload.getTags()).containsExactly("art", "photography", "exhibition");

        Assertions.assertThat(payload.getWriter()).isNotNull();
        Assertions.assertThat(payload.getWriter().getId()).isEqualTo(1L);
        Assertions.assertThat(payload.getWriter().getNickname()).isEqualTo("testuser");
        Assertions.assertThat(payload.getWriter().getProfileImage()).isEqualTo("https://example.com/profile.jpg");
    }

    @Test
    @DisplayName("of - Exhibition으로 좋아요 이벤트용 최소 정보 Payload 생성")
    void of_Exhibition으로_좋아요_이벤트용_최소_정보_Payload_생성() {
        // GIVEN
        User writer = User.builder()
                .email("test@example.com")
                .password("password123")
                .nickname("testuser")
                .profileImage("https://example.com/profile.jpg")
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .build();
        ReflectionTestUtils.setField(writer, "id", 1L);

        Exhibition exhibition = Exhibition.builder()
                .writer(writer)
                .title("Test Exhibition")
                .description("Test Exhibition Description")
                .cardColor("#FF5733")
                .build();
        ReflectionTestUtils.setField(exhibition, "id", 200L);
        ReflectionTestUtils.setField(exhibition, "likeCount", 25L);

        // WHEN
        ExhibitionPayload payload = ExhibitionPayload.of(exhibition);

        // THEN
        Assertions.assertThat(payload).isNotNull();
        Assertions.assertThat(payload.getId()).isEqualTo(200L);
        Assertions.assertThat(payload.getLikeCount()).isEqualTo(25L);
        Assertions.assertThat(payload.getWriter()).isNotNull();
        Assertions.assertThat(payload.getWriter().getId()).isEqualTo(1L);
        Assertions.assertThat(payload.getWriter().getNickname()).isEqualTo("testuser");
        Assertions.assertThat(payload.getWriter().getProfileImage()).isEqualTo("https://example.com/profile.jpg");

        // 다른 필드들은 null이어야 함
        Assertions.assertThat(payload.getTitle()).isNull();
        Assertions.assertThat(payload.getDescription()).isNull();
        Assertions.assertThat(payload.getCardColor()).isNull();
        Assertions.assertThat(payload.getViewCount()).isNull();
        Assertions.assertThat(payload.getTags()).isNull();
    }

    @Test
    @DisplayName("of - ID만으로 삭제 이벤트용 Payload 생성")
    void of_ID만으로_삭제_이벤트용_Payload_생성() {
        // GIVEN
        Long exhibitionId = 300L;

        // WHEN
        ExhibitionPayload payload = ExhibitionPayload.of(exhibitionId);

        // THEN
        Assertions.assertThat(payload).isNotNull();
        Assertions.assertThat(payload.getId()).isEqualTo(300L);

        // 다른 모든 필드는 null이어야 함
        Assertions.assertThat(payload.getWriter()).isNull();
        Assertions.assertThat(payload.getTitle()).isNull();
        Assertions.assertThat(payload.getDescription()).isNull();
        Assertions.assertThat(payload.getCardColor()).isNull();
        Assertions.assertThat(payload.getViewCount()).isNull();
        Assertions.assertThat(payload.getLikeCount()).isNull();
        Assertions.assertThat(payload.getTags()).isNull();
    }

    @Test
    @DisplayName("of - 빈 태그 리스트로 Payload 생성")
    void of_빈_태그_리스트로_Payload_생성() {
        // GIVEN
        User writer = User.builder()
                .email("test@example.com")
                .password("password123")
                .nickname("testuser")
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .build();
        ReflectionTestUtils.setField(writer, "id", 1L);

        Exhibition exhibition = Exhibition.builder()
                .writer(writer)
                .title("Test Exhibition")
                .description("Test Exhibition Description")
                .cardColor("#FF5733")
                .build();
        ReflectionTestUtils.setField(exhibition, "id", 400L);

        List<String> emptyTags = Arrays.asList();

        // WHEN
        ExhibitionPayload payload = ExhibitionPayload.of(exhibition, emptyTags);

        // THEN
        Assertions.assertThat(payload).isNotNull();
        Assertions.assertThat(payload.getTags()).isEmpty();
    }

    @Test
    @DisplayName("of - viewCount와 likeCount가 0인 경우")
    void of_viewCount와_likeCount가_0인_경우() {
        // GIVEN
        User writer = User.builder()
                .email("test@example.com")
                .password("password123")
                .nickname("testuser")
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .build();
        ReflectionTestUtils.setField(writer, "id", 1L);

        Exhibition exhibition = Exhibition.builder()
                .writer(writer)
                .title("Test Exhibition")
                .description("Test Exhibition Description")
                .cardColor("#FF5733")
                .build();
        ReflectionTestUtils.setField(exhibition, "id", 500L);
        // viewCount와 likeCount는 기본값 0L

        List<String> tagNames = Arrays.asList("test");

        // WHEN
        ExhibitionPayload payload = ExhibitionPayload.of(exhibition, tagNames);

        // THEN
        Assertions.assertThat(payload).isNotNull();
        Assertions.assertThat(payload.getViewCount()).isEqualTo(0L);
        Assertions.assertThat(payload.getLikeCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("of - 긴 description과 특수문자가 포함된 cardColor로 Payload 생성")
    void of_긴_description과_특수문자가_포함된_cardColor로_Payload_생성() {
        // GIVEN
        User writer = User.builder()
                .email("test@example.com")
                .password("password123")
                .nickname("testuser")
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .build();
        ReflectionTestUtils.setField(writer, "id", 1L);

        String longDescription = "This is a very long description that contains multiple lines and special characters !@#$%^&*()";
        String specialCardColor = "rgba(255,87,51,0.5)";

        Exhibition exhibition = Exhibition.builder()
                .writer(writer)
                .title("Special Exhibition")
                .description(longDescription)
                .cardColor(specialCardColor)
                .build();
        ReflectionTestUtils.setField(exhibition, "id", 600L);

        List<String> tagNames = Arrays.asList("special");

        // WHEN
        ExhibitionPayload payload = ExhibitionPayload.of(exhibition, tagNames);

        // THEN
        Assertions.assertThat(payload).isNotNull();
        Assertions.assertThat(payload.getDescription()).isEqualTo(longDescription);
        Assertions.assertThat(payload.getCardColor()).isEqualTo(specialCardColor);
    }
}