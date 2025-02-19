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
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Document(indexName = "exhibitions", writeTypeHint = WriteTypeHint.FALSE)
public class ExhibitionSearch {
    @Id
    @Field(name = "id", type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Long, index = false)
    private Long writerId;

    @Field(type = FieldType.Keyword)
    private String writerNickname;

    @Field(type = FieldType.Keyword, index = false)
    private String writerProfileImage;

    @Field(type = FieldType.Keyword, index = false)
    private String introduction;

    @Field(type = FieldType.Integer, index = false)
    private Integer participants;

    @Field(type = FieldType.Keyword, index = false)
    private String cardColor;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text, index = false)
    private String description;

    @Field(type = FieldType.Text)
    private List<String> tags;

    @Field(type = FieldType.Long)
    private Long likeCount;

    @Field(type = FieldType.Long)
    private Long viewCount;

    @Field(type = FieldType.Long)
    private Long commentCount;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createdAt;

    // SingleWorkSearch는 JPA가 관리하는 영속성 객체가 아니기 때문에 엔티티 클래스 내부에 변환메서드 작성했음
    public static ExhibitionSearch of(
            final Exhibition exhibition,
            final User writer,
            final List<String> tags
    ) {
        return ExhibitionSearch.builder()
                .id(exhibition.getId())
                .writerId(writer.getId())
                .writerNickname(writer.getNickname())
                .writerProfileImage(writer.getProfileImage())
                .title(exhibition.getTitle())
                .cardColor(exhibition.getCardColor())
                .description(exhibition.getDescription())
                .tags(tags)
                .likeCount(0L)
                .viewCount(0L)
                .commentCount(0L)
                .createdAt(exhibition.getCreatedAt())
                .build();
    }

    public void incrementViewCount() {
        viewCount++;
    }

    public void updateLikeCount(final Long likeCount) {
        this.likeCount = likeCount;
    }

    public void updateCommentCount(final Long commentCount) {
        this.commentCount = commentCount;
    }
}

