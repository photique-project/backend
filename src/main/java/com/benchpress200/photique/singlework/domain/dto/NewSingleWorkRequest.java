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
public class NewSingleWorkRequest {
    @NotNull(message = "Id must not be null")
    @Id
    private Long writerId;

    @NotNull(message = "Image must not be null")
    @Image
    private MultipartFile image;

    @NotBlank(message = "The camera name must not be blank.")
    @Size(max = 50, message = "The camera name must not exceed 50 characters")
    private String camera;

    @NotBlank(message = "The lens name must not be blank.")
    @Size(max = 50, message = "The lens name must not exceed 50 characters")
    private String lens;

    @NotNull(message = "The value of aperture must not be null")
    @Enum(enumClass = Aperture.class, message = "Invalid value of aperture")
    private String aperture;

    @NotNull(message = "The value of shutterSpeed must not be null")
    @Enum(enumClass = ShutterSpeed.class, message = "Invalid value of shutterSpeed")
    private String shutterSpeed;

    @NotNull(message = "The value of iso must not be null")
    @Enum(enumClass = ISO.class, message = "Invalid value of iso")
    private String iso;

    @NotBlank(message = "The location must not be blank.")
    @Size(max = 50, message = "The location must not exceed 50 characters")
    private String location;

    @NotNull(message = "The value of category must not be null")
    @Enum(enumClass = ISO.class, message = "Invalid value of category")
    private String category;

    @NotNull(message = "The date must not be blank.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private List<NewTagRequest> tags;

    @NotBlank(message = "The title must not be blank.")
    @Size(max = 30, message = "The title must not exceed 30 characters")
    private String title;

    @NotBlank(message = "The description must not be blank.")
    @Size(max = 500, message = "The description must not exceed 30 characters")
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
