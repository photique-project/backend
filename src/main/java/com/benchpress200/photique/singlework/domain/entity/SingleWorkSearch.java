package com.benchpress200.photique.singlework.domain.entity;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Document(indexName = "singleworks")
public class SingleWorkSearch {
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

    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;
}
