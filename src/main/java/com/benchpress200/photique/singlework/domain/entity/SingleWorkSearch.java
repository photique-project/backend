package com.benchpress200.photique.singlework.domain.entity;

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
@Document(indexName = "singleworks", writeTypeHint = WriteTypeHint.FALSE)
public class SingleWorkSearch {
    // 필드타입
    // Text => 분석 + 텍스트 전체 검색
    // Keyword => 분석되지 않고 정확한 일치 검색

    @Id
    @Field(name = "id", type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Keyword, index = false)
    private String image;

    @Field(type = FieldType.Long, index = false)
    private Long writerId;

    @Field(type = FieldType.Keyword)
    private String writerNickname;

    @Field(type = FieldType.Keyword, index = false)
    private String writerProfileImage;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private List<String> tags;

    @Field(type = FieldType.Text)
    private String category;

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

    public void updateLikeCount(final Long likeCount) {
        this.likeCount = likeCount;
    }

    public void updateCommentCount(final Long commentCount) {
        this.commentCount = commentCount;
    }

    public void incrementViewCount() {
        viewCount++;
    }

    public void updateImage(final String image) {
        this.image = image;
    }

    public void updateCategory(final String category) {
        this.category = category;
    }

    public void updateTags(final List<String> tags) {
        this.tags = tags;
    }

    public void updateTitle(final String title) {
        this.title = title;
    }


    // SingleWorkSearch는 JPA가 관리하는 영속성 객체가 아니기 때문에 엔티티 클래스 내부에 변환메서드 작성했음
    public static SingleWorkSearch of(
            final SingleWork singleWork,
            final User writer,
            final List<String> tags
    ) {
        return SingleWorkSearch.builder()
                .id(singleWork.getId())
                .image(singleWork.getImage())
                .writerId(writer.getId())
                .writerNickname(writer.getNickname())
                .writerProfileImage(writer.getProfileImage())
                .title(singleWork.getTitle())
                .tags(tags)
                .category(singleWork.getCategory().getValue())
                .likeCount(0L)
                .viewCount(singleWork.getViewCount())
                .commentCount(0L)
                .createdAt(singleWork.getCreatedAt())
                .build();
    }
}
