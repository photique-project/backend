package com.benchpress200.photique.singlework.domain.dto;

import com.benchpress200.photique.common.domain.dto.NewTagRequest;
import com.benchpress200.photique.common.domain.entity.Tag;
import com.benchpress200.photique.common.dtovalidator.annotation.Enum;
import com.benchpress200.photique.common.dtovalidator.annotation.Id;
import com.benchpress200.photique.common.dtovalidator.annotation.Image;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;
import com.benchpress200.photique.singlework.domain.enumeration.Aperture;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.ISO;
import com.benchpress200.photique.singlework.domain.enumeration.ShutterSpeed;
import com.benchpress200.photique.user.domain.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class SingleWorkCreateRequest {
    @NotNull(message = "Id must not be null")
    @Id
    private Long writerId;

    @NotNull(message = "Image must not be null")
    @Image
    private MultipartFile image;

    @Size(max = 50, message = "Camera name must not exceed 50 characters")
    private String camera;

    @Size(max = 50, message = "Lens name must not exceed 50 characters")
    private String lens;

    @Enum(enumClass = Aperture.class, message = "Invalid value of aperture")
    private String aperture;

    @Enum(enumClass = ShutterSpeed.class, message = "Invalid value of shutterSpeed")
    private String shutterSpeed;

    @Enum(enumClass = ISO.class, message = "Invalid value of iso")
    private String iso;

    @Size(max = 50, message = "Location must not exceed 50 characters")
    private String location;

    @NotNull(message = "Category must not be null")
    @Enum(enumClass = Category.class, message = "Invalid value of category")
    private String category;

    @NotNull(message = "Date must not be blank.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Size(max = 5, message = "Tag list size must be between 0 and 5")
    @Valid
    private List<NewTagRequest> tags;

    @NotBlank(message = "Title must not be blank.")
    @Size(max = 30, message = "Title must not exceed 30 characters")
    private String title;

    @NotBlank(message = "Description must not be blank.")
    @Size(max = 500, message = "Description must not exceed 30 characters")
    private String description;

    public boolean hasTags() {
        return tags != null && !tags.isEmpty();
    }

    public SingleWork toSingleWorkEntity(
            final User writer,
            final String imageUrl
    ) {
        return SingleWork.builder()
                .writer(writer)
                .image(imageUrl)
                .camera(camera)
                .lens(lens)
                .aperture(Aperture.fromValue(aperture))
                .shutterSpeed(ShutterSpeed.fromValue(shutterSpeed))
                .iso(ISO.fromValue(iso))
                .location(location)
                .category(Category.fromValue(category))
                .date(date)
                .title(title)
                .description(description)
                .build();
    }

    public List<SingleWorkTag> toSingleWorkTagEntities(
            final SingleWork singleWork,
            final List<Tag> tags
    ) {
        return tags.stream()
                .map(tag -> SingleWorkTag.builder()
                        .singleWork(singleWork)
                        .tag(tag)
                        .build())
                .toList();
    }
}
