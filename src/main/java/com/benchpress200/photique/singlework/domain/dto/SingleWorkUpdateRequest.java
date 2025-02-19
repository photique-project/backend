package com.benchpress200.photique.singlework.domain.dto;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;
import com.benchpress200.photique.singlework.domain.enumeration.Aperture;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.ISO;
import com.benchpress200.photique.singlework.domain.enumeration.ShutterSpeed;
import com.benchpress200.photique.singlework.validation.annotation.Enum;
import com.benchpress200.photique.singlework.validation.annotation.Image;
import com.benchpress200.photique.tag.domain.dto.NewTagRequest;
import com.benchpress200.photique.tag.domain.entity.Tag;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class SingleWorkUpdateRequest {

    private Long id;

    @Image
    private MultipartFile image; // 업데이트에서 null 이면 수정하지 않는 요청

    @Size(min = 1, max = 50, message = "Camera name must not exceed 50 characters")
    private String camera; // 필수값이지만 업데이트에서는 수정 안한다면 null로 요청

    @Size(max = 50, message = "Lens name must not exceed 50 characters")
    private String lens; // null이면 수정 x, null이 아닌 빈 값 들어오면 기본값세팅

    @Enum(enumClass = Aperture.class, message = "Invalid value of aperture")
    private String aperture; // null이면 수정 x, null이 아닌 빈 값 들어오면 기본값세팅

    @Enum(enumClass = ShutterSpeed.class, message = "Invalid value of shutterSpeed")
    private String shutterSpeed; // null이면 수정 x, null이 아닌 빈 값 들어오면 기본값세팅

    @Enum(enumClass = ISO.class, message = "Invalid value of iso")
    private String iso; // null이면 수정 x, null이 아닌 빈 값 들어오면 기본값세팅

    @Size(max = 50, message = "Location must not exceed 50 characters")
    private String location; // null이면 수정 x, null이 아닌 빈 값 들어오면 기본값세팅

    @Enum(enumClass = Category.class, message = "Invalid value of category")
    private String category; // null이면 수정 x

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date; // null이면 수정 x

    @Size(max = 5, message = "The number of tag must be between 0 and 5")
    private List<NewTagRequest> tags; // null이면 수정 x

    @Size(min = 1, max = 30, message = "Title must not exceed 30 characters")
    private String title; // null이면 수정 x

    @Size(min = 1, max = 500, message = "Description must not exceed 30 characters")
    private String description; // null이면 수정 x

    public List<String> getTags() {
        if (tags == null) {
            return null;
        }

        return tags.stream().map(NewTagRequest::getName).toList();
    }

    public List<SingleWorkTag> toSingleWorkTagEntities(
            final SingleWork singleWork,
            final List<Tag> tags
    ) {
        // null 일 경우 유지를 위한 null반환
        if (tags == null) {
            return null;
        }

        return tags.stream()
                .map(tag -> SingleWorkTag.builder()
                        .singleWork(singleWork)
                        .tag(tag)
                        .build())
                .toList();
    }


    public void withSingleWorkId(Long id) {
        this.id = id;
    }

    public boolean isEmptyLens() {
        return lens.isEmpty();
    }


    public boolean isEmptyAperture() {
        return aperture.isEmpty();
    }


    public boolean isEmptyShutterSpeed() {
        return shutterSpeed.isEmpty();
    }


    public boolean isEmptyIso() {
        return iso.isEmpty();
    }


    public boolean isEmptyLocation() {
        return location.isEmpty();
    }


    public boolean hasTags() {
        return tags != null;
    }

    public boolean hasTitle() {
        return title != null;
    }

    public boolean isEmptyTitle() {
        return title.isEmpty();
    }

    public boolean hasDescription() {
        return description != null;
    }

    public boolean isEmptyDescription() {
        return description.isEmpty();
    }
}
