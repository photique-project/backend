package com.benchpress200.photique.singlework.application.query.result;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.enumeration.Aperture;
import com.benchpress200.photique.singlework.domain.enumeration.ISO;
import com.benchpress200.photique.singlework.domain.enumeration.ShutterSpeed;
import com.benchpress200.photique.tag.domain.entity.Tag;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SingleWorkDetailsResult {
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
    private LocalDateTime createdAt;
    private boolean isLiked;
    private boolean isFollowing;

    public static SingleWorkDetailsResult of(
            SingleWork singleWork,
            List<Tag> tags,
            boolean isLiked,
            boolean isFollowing
    ) {
        Aperture aperture = singleWork.getAperture();
        String apertureValue = aperture == null ? null : aperture.getValue();

        ShutterSpeed shutterSpeed = singleWork.getShutterSpeed();
        String shutterSpeedValue = shutterSpeed == null ? null : shutterSpeed.getValue();

        ISO iso = singleWork.getIso();
        String isoValue = iso == null ? null : iso.getValue();

        List<String> tagNames = tags.stream()
                .map(Tag::getName)
                .toList();

        return SingleWorkDetailsResult.builder()
                .id(singleWork.getId())
                .writer(Writer.from(singleWork.getWriter()))
                .title(singleWork.getTitle())
                .description(singleWork.getDescription())
                .image(singleWork.getImage())
                .camera(singleWork.getCamera())
                .lens(singleWork.getLens())
                .aperture(apertureValue)
                .shutterSpeed(shutterSpeedValue)
                .iso(isoValue)
                .category(singleWork.getCategory().getValue())
                .location(singleWork.getLocation())
                .date(singleWork.getDate())
                .tags(tagNames)
                .likeCount(singleWork.getLikeCount())
                .viewCount(singleWork.getViewCount())
                .createdAt(singleWork.getCreatedAt())
                .isLiked(isLiked)
                .isFollowing(isFollowing)
                .build();
    }
}
