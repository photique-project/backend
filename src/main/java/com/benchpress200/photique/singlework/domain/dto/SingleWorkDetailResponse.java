package com.benchpress200.photique.singlework.domain.dto;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.tag.domain.dto.TagResponse;
import com.benchpress200.photique.tag.domain.entity.Tag;
import com.benchpress200.photique.user.domain.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SingleWorkDetailResponse {
    private Long id;
    private Writer writer;
    private String image;
    private String camera;
    private String lens;
    private String aperture;
    private String shutterSpeed;
    private String iso;
    private String location;
    private String category;
    private LocalDate date;
    private List<TagResponse> tags;
    private String title;
    private String description;
    private Long likeCount;
    private Long viewCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
    @JsonProperty("isLiked")
    private boolean isLiked;
    @JsonProperty("isFollowing")
    private boolean isFollowing;


    @Builder
    record Writer(
            Long id,
            String nickname,
            String profileImage,
            String introduction
    ) {
        public static Writer from(final User writer) {
            return Writer.builder()
                    .id(writer.getId())
                    .nickname(writer.getNickname())
                    .profileImage(writer.getProfileImage())
                    .introduction(writer.getIntroduction())
                    .build();
        }
    }

    public static SingleWorkDetailResponse of(
            final SingleWork singleWork,
            final List<Tag> tags,
            final Long likeCount,
            final boolean isLiked,
            final boolean isFollowing
    ) {
        List<TagResponse> tagsResponse = tags.stream()
                .map(TagResponse::from)
                .toList();

        return SingleWorkDetailResponse.builder()
                .id(singleWork.getId())
                .writer(Writer.from(singleWork.getWriter()))
                .image(singleWork.getImage())
                .camera(singleWork.getCamera())
                .lens(singleWork.getLens())
                .aperture(singleWork.getAperture().getValue())
                .shutterSpeed(singleWork.getShutterSpeed().getValue())
                .iso(singleWork.getIso().getValue())
                .location(singleWork.getLocation())
                .category(singleWork.getCategory().getValue())
                .date(singleWork.getDate())
                .tags(tagsResponse)
                .title(singleWork.getTitle())
                .description(singleWork.getDescription())
                .likeCount(likeCount)
                .viewCount(singleWork.getViewCount())
                .createdAt(singleWork.getCreatedAt())
                .isLiked(isLiked)
                .isFollowing(isFollowing)
                .build();
    }
}

