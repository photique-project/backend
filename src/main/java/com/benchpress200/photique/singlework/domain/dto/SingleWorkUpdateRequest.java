package com.benchpress200.photique.singlework.domain.dto;

import com.benchpress200.photique.common.domain.dto.NewTagRequest;
import com.benchpress200.photique.common.dtovalidator.annotation.Enum;
import com.benchpress200.photique.common.dtovalidator.annotation.Image;
import com.benchpress200.photique.singlework.domain.enumeration.Aperture;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.ISO;
import com.benchpress200.photique.singlework.domain.enumeration.ShutterSpeed;
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

    @Size(max = 50, message = "The camera name must not exceed 50 characters")
    private String camera; // 필수값이지만 업데이트에서는 수정 안한다면 null로 요청

    @Size(max = 50, message = "The lens name must not exceed 50 characters")
    private String lens; // null이면 수정 x, null이 아닌 빈 값 들어오면 기본값세팅

    @Enum(enumClass = Aperture.class, message = "Invalid value of aperture")
    private String aperture; // null이면 수정 x, null이 아닌 빈 값 들어오면 기본값세팅

    @Enum(enumClass = ShutterSpeed.class, message = "Invalid value of shutterSpeed")
    private String shutterSpeed; // null이면 수정 x, null이 아닌 빈 값 들어오면 기본값세팅

    @Enum(enumClass = ISO.class, message = "Invalid value of iso")
    private String iso; // null이면 수정 x, null이 아닌 빈 값 들어오면 기본값세팅

    @Size(max = 50, message = "The location must not exceed 50 characters")
    private String location; // null이면 수정 x, null이 아닌 빈 값 들어오면 기본값세팅

    @Enum(enumClass = Category.class, message = "Invalid value of category")
    private String category; // null이면 수정 x

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date; // null이면 수정 x

    @Size(max = 5, message = "The list size must be between 0 and 5")
    private List<NewTagRequest> tags; // null이면 수정 x

    @Size(max = 30, message = "The title must not exceed 30 characters")
    private String title; // null이면 수정 x

    @Size(max = 500, message = "The description must not exceed 30 characters")
    private String description; // null이면 수정 x

    public void withSingleWorkId(Long id) {
        this.id = id;
    }

    public boolean hasImage() {
        return image != null;
    }

    public boolean isEmptyImage() {
        return image.isEmpty();
    }

    public boolean hasCamera() {
        return camera != null;
    }

    public boolean isEmptyCamera() {
        return camera.isEmpty();
    }

    public boolean hasLens() {
        return lens != null;
    }

    public boolean isEmptyLens() {
        return lens.isEmpty();
    }

    public boolean hasAperture() {
        return aperture != null;
    }

    public boolean isEmptyAperture() {
        return aperture.isEmpty();
    }

    public boolean hasShutterSpeed() {
        return shutterSpeed != null;
    }

    public boolean isEmptyShutterSpeed() {
        return shutterSpeed.isEmpty();
    }

    public boolean hasIso() {
        return iso != null;
    }

    public boolean isEmptyIso() {
        return iso.isEmpty();
    }

    public boolean hasLocation() {
        return location != null;
    }

    public boolean isEmptyLocation() {
        return location.isEmpty();
    }

    public boolean hasCategory() {
        return category != null;
    }

    public boolean isEmptyCategory() {
        return category.isEmpty();
    }

    public boolean hasDate() {
        return date != null;
    }

    public boolean hasTags() {
        return tags != null;
    }

    public boolean isEmptyTags() {
        return tags.isEmpty();
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
