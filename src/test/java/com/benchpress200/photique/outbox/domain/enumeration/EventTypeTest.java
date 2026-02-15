package com.benchpress200.photique.outbox.domain.enumeration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("EventType 테스트")
public class EventTypeTest {

    @Test
    @DisplayName("from - 유효한 값으로 EventType 변환 성공 - CREATED")
    void from_유효한_값으로_EventType_변환_성공_CREATED() {
        // GIVEN
        String value = "created";

        // WHEN
        EventType result = EventType.from(value);

        // THEN
        Assertions.assertThat(result).isEqualTo(EventType.CREATED);
        Assertions.assertThat(result.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("from - 유효한 값으로 EventType 변환 성공 - UPDATED")
    void from_유효한_값으로_EventType_변환_성공_UPDATED() {
        // GIVEN
        String value = "updated";

        // WHEN
        EventType result = EventType.from(value);

        // THEN
        Assertions.assertThat(result).isEqualTo(EventType.UPDATED);
        Assertions.assertThat(result.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("from - 유효한 값으로 EventType 변환 성공 - LIKED")
    void from_유효한_값으로_EventType_변환_성공_LIKED() {
        // GIVEN
        String value = "liked";

        // WHEN
        EventType result = EventType.from(value);

        // THEN
        Assertions.assertThat(result).isEqualTo(EventType.LIKED);
        Assertions.assertThat(result.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("from - 유효한 값으로 EventType 변환 성공 - UNLIKED")
    void from_유효한_값으로_EventType_변환_성공_UNLIKED() {
        // GIVEN
        String value = "unliked";

        // WHEN
        EventType result = EventType.from(value);

        // THEN
        Assertions.assertThat(result).isEqualTo(EventType.UNLIKED);
        Assertions.assertThat(result.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("from - 유효한 값으로 EventType 변환 성공 - DELETED")
    void from_유효한_값으로_EventType_변환_성공_DELETED() {
        // GIVEN
        String value = "deleted";

        // WHEN
        EventType result = EventType.from(value);

        // THEN
        Assertions.assertThat(result).isEqualTo(EventType.DELETED);
        Assertions.assertThat(result.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("from - 잘못된 값으로 예외 발생")
    void from_잘못된_값으로_예외_발생() {
        // GIVEN
        String invalidValue = "invalid_event";

        // WHEN & THEN
        Assertions.assertThatThrownBy(() -> EventType.from(invalidValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown EventType: " + invalidValue);
    }

    @Test
    @DisplayName("from - null 값으로 예외 발생")
    void from_null_값으로_예외_발생() {
        // GIVEN
        String nullValue = null;

        // WHEN & THEN
        Assertions.assertThatThrownBy(() -> EventType.from(nullValue))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("from - 빈 문자열로 예외 발생")
    void from_빈_문자열로_예외_발생() {
        // GIVEN
        String emptyValue = "";

        // WHEN & THEN
        Assertions.assertThatThrownBy(() -> EventType.from(emptyValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown EventType: " + emptyValue);
    }

    @Test
    @DisplayName("getValue - 모든 EventType의 value 확인")
    void getValue_모든_EventType의_value_확인() {
        // WHEN & THEN
        Assertions.assertThat(EventType.CREATED.getValue()).isEqualTo("created");
        Assertions.assertThat(EventType.UPDATED.getValue()).isEqualTo("updated");
        Assertions.assertThat(EventType.LIKED.getValue()).isEqualTo("liked");
        Assertions.assertThat(EventType.UNLIKED.getValue()).isEqualTo("unliked");
        Assertions.assertThat(EventType.DELETED.getValue()).isEqualTo("deleted");
    }

    @Test
    @DisplayName("from - 대소문자 구분 확인")
    void from_대소문자_구분_확인() {
        // GIVEN
        String upperCaseValue = "CREATED";

        // WHEN & THEN
        Assertions.assertThatThrownBy(() -> EventType.from(upperCaseValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown EventType: " + upperCaseValue);
    }
}