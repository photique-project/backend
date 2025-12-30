package com.benchpress200.photique.singlework.api.query.response;

import com.benchpress200.photique.singlework.application.query.result.SingleWorkDetailsResult;
import com.benchpress200.photique.singlework.application.query.result.Writer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SingleWorkDetailsResponse {
    private Long id;
    private Writer writer;
    private String title;
    private String description;
    private String image;
    private String camera;
    private String lens;
    private String aperture;
    private String shutterSpeed;
    private String iso;
    private String category;
    private String location;
    private LocalDate date;
    private List<String> tags;
    private Long likeCount;
    private Long viewCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
    @JsonProperty("isLiked")
    private boolean isLiked;
    @JsonProperty("isFollowing")
    private boolean isFollowing;

    public static SingleWorkDetailsResponse from(SingleWorkDetailsResult singleWorkDetailsResult) {
        return SingleWorkDetailsResponse.builder()
                .id(singleWorkDetailsResult.getId())
                .writer(singleWorkDetailsResult.getWriter())
                .title(singleWorkDetailsResult.getTitle())
                .description(singleWorkDetailsResult.getDescription())
                .image(singleWorkDetailsResult.getImage())
                .camera(singleWorkDetailsResult.getCamera())
                .lens(singleWorkDetailsResult.getLens())
                .aperture(singleWorkDetailsResult.getAperture())
                .shutterSpeed(singleWorkDetailsResult.getShutterSpeed())
                .iso(singleWorkDetailsResult.getIso())
                .category(singleWorkDetailsResult.getCategory())
                .location(singleWorkDetailsResult.getLocation())
                .date(singleWorkDetailsResult.getDate())
                .build();
    }
}
