package com.benchpress200.photique.outbox.application.payload;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.enumeration.Provider;
import com.benchpress200.photique.user.domain.enumeration.Role;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("SingleWorkPayload 테스트")
public class SingleWorkPayloadTest {

    @Test
    @DisplayName("of - SingleWork와 태그 리스트로 전체 정보를 가진 Payload 생성")
    void of_SingleWork와_태그_리스트로_전체_정보를_가진_Payload_생성() {
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

        SingleWork singleWork = SingleWork.builder()
                .writer(writer)
                .title("Test Title")
                .description("Test Description")
                .image("https://example.com/image.jpg")
                .camera("Canon EOS R5")
                .category(Category.LANDSCAPE)
                .date(LocalDate.of(2024, 1, 15))
                .build();
        ReflectionTestUtils.setField(singleWork, "id", 100L);
        ReflectionTestUtils.setField(singleWork, "viewCount", 50L);
        ReflectionTestUtils.setField(singleWork, "likeCount", 10L);

        List<String> tagNames = Arrays.asList("nature", "landscape", "photography");

        // WHEN
        SingleWorkPayload payload = SingleWorkPayload.of(singleWork, tagNames);

        // THEN
        Assertions.assertThat(payload).isNotNull();
        Assertions.assertThat(payload.getId()).isEqualTo(100L);
        Assertions.assertThat(payload.getTitle()).isEqualTo("Test Title");
        Assertions.assertThat(payload.getDescription()).isEqualTo("Test Description");
        Assertions.assertThat(payload.getImage()).isEqualTo("https://example.com/image.jpg");
        Assertions.assertThat(payload.getCategory()).isEqualTo(Category.LANDSCAPE.getValue());
        Assertions.assertThat(payload.getViewCount()).isEqualTo(50L);
        Assertions.assertThat(payload.getLikeCount()).isEqualTo(10L);
        Assertions.assertThat(payload.getTags()).containsExactly("nature", "landscape", "photography");

        Assertions.assertThat(payload.getWriter()).isNotNull();
        Assertions.assertThat(payload.getWriter().getId()).isEqualTo(1L);
        Assertions.assertThat(payload.getWriter().getNickname()).isEqualTo("testuser");
        Assertions.assertThat(payload.getWriter().getProfileImage()).isEqualTo("https://example.com/profile.jpg");
    }

    @Test
    @DisplayName("of - SingleWork로 좋아요 이벤트용 최소 정보 Payload 생성")
    void of_SingleWork로_좋아요_이벤트용_최소_정보_Payload_생성() {
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

        SingleWork singleWork = SingleWork.builder()
                .writer(writer)
                .title("Test Title")
                .description("Test Description")
                .image("https://example.com/image.jpg")
                .camera("Canon EOS R5")
                .category(Category.PORTRAIT)
                .date(LocalDate.now())
                .build();
        ReflectionTestUtils.setField(singleWork, "id", 200L);
        ReflectionTestUtils.setField(singleWork, "likeCount", 25L);

        // WHEN
        SingleWorkPayload payload = SingleWorkPayload.of(singleWork);

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
        Assertions.assertThat(payload.getImage()).isNull();
        Assertions.assertThat(payload.getCategory()).isNull();
        Assertions.assertThat(payload.getViewCount()).isNull();
        Assertions.assertThat(payload.getTags()).isNull();
    }

    @Test
    @DisplayName("of - ID만으로 삭제 이벤트용 Payload 생성")
    void of_ID만으로_삭제_이벤트용_Payload_생성() {
        // GIVEN
        Long singleWorkId = 300L;

        // WHEN
        SingleWorkPayload payload = SingleWorkPayload.of(singleWorkId);

        // THEN
        Assertions.assertThat(payload).isNotNull();
        Assertions.assertThat(payload.getId()).isEqualTo(300L);

        // 다른 모든 필드는 null이어야 함
        Assertions.assertThat(payload.getWriter()).isNull();
        Assertions.assertThat(payload.getTitle()).isNull();
        Assertions.assertThat(payload.getDescription()).isNull();
        Assertions.assertThat(payload.getImage()).isNull();
        Assertions.assertThat(payload.getCategory()).isNull();
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

        SingleWork singleWork = SingleWork.builder()
                .writer(writer)
                .title("Test Title")
                .description("Test Description")
                .image("https://example.com/image.jpg")
                .camera("Canon EOS R5")
                .category(Category.STREET)
                .date(LocalDate.now())
                .build();
        ReflectionTestUtils.setField(singleWork, "id", 400L);

        List<String> emptyTags = Arrays.asList();

        // WHEN
        SingleWorkPayload payload = SingleWorkPayload.of(singleWork, emptyTags);

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

        SingleWork singleWork = SingleWork.builder()
                .writer(writer)
                .title("Test Title")
                .description("Test Description")
                .image("https://example.com/image.jpg")
                .camera("Canon EOS R5")
                .category(Category.PORTRAIT)
                .date(LocalDate.now())
                .build();
        ReflectionTestUtils.setField(singleWork, "id", 500L);
        // viewCount와 likeCount는 기본값 0L

        List<String> tagNames = Arrays.asList("test");

        // WHEN
        SingleWorkPayload payload = SingleWorkPayload.of(singleWork, tagNames);

        // THEN
        Assertions.assertThat(payload).isNotNull();
        Assertions.assertThat(payload.getViewCount()).isEqualTo(0L);
        Assertions.assertThat(payload.getLikeCount()).isEqualTo(0L);
    }
}