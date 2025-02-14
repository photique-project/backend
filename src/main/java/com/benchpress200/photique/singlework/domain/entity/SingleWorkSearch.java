package com.benchpress200.photique.singlework.domain.entity;

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
}
