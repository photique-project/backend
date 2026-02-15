package com.benchpress200.photique.outbox.domain.enumeration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AggregateType 테스트")
public class AggregateTypeTest {

    @Test
    @DisplayName("from - 유효한 값으로 AggregateType 변환 성공 - SINGLEWORK")
    void from_유효한_값으로_AggregateType_변환_성공_SINGLEWORK() {
        // GIVEN
        String value = "singlework";

        // WHEN
        AggregateType result = AggregateType.from(value);

        // THEN
        Assertions.assertThat(result).isEqualTo(AggregateType.SINGLEWORK);
        Assertions.assertThat(result.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("from - 유효한 값으로 AggregateType 변환 성공 - SINGLEWORK_COMMENT")
    void from_유효한_값으로_AggregateType_변환_성공_SINGLEWORK_COMMENT() {
        // GIVEN
        String value = "singlework.comment";

        // WHEN
        AggregateType result = AggregateType.from(value);

        // THEN
        Assertions.assertThat(result).isEqualTo(AggregateType.SINGLEWORK_COMMENT);
        Assertions.assertThat(result.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("from - 유효한 값으로 AggregateType 변환 성공 - EXHIBITION")
    void from_유효한_값으로_AggregateType_변환_성공_EXHIBITION() {
        // GIVEN
        String value = "exhibition";

        // WHEN
        AggregateType result = AggregateType.from(value);

        // THEN
        Assertions.assertThat(result).isEqualTo(AggregateType.EXHIBITION);
        Assertions.assertThat(result.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("from - 유효한 값으로 AggregateType 변환 성공 - EXHIBITION_COMMENT")
    void from_유효한_값으로_AggregateType_변환_성공_EXHIBITION_COMMENT() {
        // GIVEN
        String value = "exhibition.comment";

        // WHEN
        AggregateType result = AggregateType.from(value);

        // THEN
        Assertions.assertThat(result).isEqualTo(AggregateType.EXHIBITION_COMMENT);
        Assertions.assertThat(result.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("from - 유효한 값으로 AggregateType 변환 성공 - FOLLOW")
    void from_유효한_값으로_AggregateType_변환_성공_FOLLOW() {
        // GIVEN
        String value = "follow";

        // WHEN
        AggregateType result = AggregateType.from(value);

        // THEN
        Assertions.assertThat(result).isEqualTo(AggregateType.FOLLOW);
        Assertions.assertThat(result.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("from - 유효한 값으로 AggregateType 변환 성공 - USER")
    void from_유효한_값으로_AggregateType_변환_성공_USER() {
        // GIVEN
        String value = "user";

        // WHEN
        AggregateType result = AggregateType.from(value);

        // THEN
        Assertions.assertThat(result).isEqualTo(AggregateType.USER);
        Assertions.assertThat(result.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("from - 잘못된 값으로 예외 발생")
    void from_잘못된_값으로_예외_발생() {
        // GIVEN
        String invalidValue = "invalid_type";

        // WHEN & THEN
        Assertions.assertThatThrownBy(() -> AggregateType.from(invalidValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown AggregateType: " + invalidValue);
    }

    @Test
    @DisplayName("from - null 값으로 예외 발생")
    void from_null_값으로_예외_발생() {
        // GIVEN
        String nullValue = null;

        // WHEN & THEN
        Assertions.assertThatThrownBy(() -> AggregateType.from(nullValue))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("from - 빈 문자열로 예외 발생")
    void from_빈_문자열로_예외_발생() {
        // GIVEN
        String emptyValue = "";

        // WHEN & THEN
        Assertions.assertThatThrownBy(() -> AggregateType.from(emptyValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown AggregateType: " + emptyValue);
    }

    @Test
    @DisplayName("getValue - 모든 AggregateType의 value 확인")
    void getValue_모든_AggregateType의_value_확인() {
        // WHEN & THEN
        Assertions.assertThat(AggregateType.SINGLEWORK.getValue()).isEqualTo("singlework");
        Assertions.assertThat(AggregateType.SINGLEWORK_COMMENT.getValue()).isEqualTo("singlework.comment");
        Assertions.assertThat(AggregateType.EXHIBITION.getValue()).isEqualTo("exhibition");
        Assertions.assertThat(AggregateType.EXHIBITION_COMMENT.getValue()).isEqualTo("exhibition.comment");
        Assertions.assertThat(AggregateType.FOLLOW.getValue()).isEqualTo("follow");
        Assertions.assertThat(AggregateType.USER.getValue()).isEqualTo("user");
    }
}