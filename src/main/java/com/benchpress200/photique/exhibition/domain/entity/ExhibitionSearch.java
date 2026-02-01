package com.benchpress200.photique.exhibition.domain.entity;

import com.benchpress200.photique.user.domain.entity.User;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Document(
        indexName = "exhibitions",
        writeTypeHint = WriteTypeHint.FALSE
)
@Setting(settingPath = "elasticsearch/settings.json")
@Mapping(mappingPath = "elasticsearch/exhibitions-mappings.json")
public class ExhibitionSearch {
    /*
     * === 필드 타입 ===
     * Text: 분석 + 텍스트 전체 검색
     * Keyword: 분석되지 않고 정확한 일치 검색
     */

    @Id
    @Field(type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Object)
    private Writer writer;

    @Field(type = FieldType.Keyword, index = false)
    private String cardColor;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Text)
    private List<String> tags;

    @Field(type = FieldType.Long)
    private Long likeCount;

    @Field(type = FieldType.Long)
    private Long viewCount;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createdAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime updatedAt;

    @Field(type = FieldType.Long)
    private Long lastProcessedOutboxEventId;

    public static ExhibitionSearch of(
            Exhibition exhibition,
            List<String> tags
    ) {
        User writer = exhibition.getWriter();

        return ExhibitionSearch.builder()
                .id(exhibition.getId())
                .writer(Writer.from(writer))
                .title(exhibition.getTitle())
                .cardColor(exhibition.getCardColor())
                .description(exhibition.getDescription())
                .tags(tags)
                .likeCount(exhibition.getLikeCount())
                .viewCount(exhibition.getViewCount())
                .createdAt(exhibition.getCreatedAt())
                .build();
    }

    public Long getWriterId() {
        return writer.getId();
    }

    public String getWriterNickname() {
        return writer.getNickname();
    }

    public String getWriterProfileImage() {
        return writer.getProfileImage();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class Writer {
        @Field(type = FieldType.Long)
        private Long id;

        @Field(type = FieldType.Keyword)
        private String nickname;

        @Field(type = FieldType.Keyword, index = false)
        private String profileImage;

        public static Writer from(User writer) {
            return Writer.builder()
                    .id(writer.getId())
                    .nickname(writer.getNickname())
                    .profileImage(writer.getProfileImage())
                    .build();
        }
    }
}

