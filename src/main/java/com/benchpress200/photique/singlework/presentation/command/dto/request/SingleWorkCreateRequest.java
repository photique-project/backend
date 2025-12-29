package com.benchpress200.photique.singlework.presentation.command.dto.request;

import com.benchpress200.photique.image.presentation.validator.ImageValidator;
import com.benchpress200.photique.singlework.application.command.model.SingleWorkCreateCommand;
import com.benchpress200.photique.singlework.domain.enumeration.Aperture;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.ISO;
import com.benchpress200.photique.singlework.domain.enumeration.ShutterSpeed;
import com.benchpress200.photique.singlework.presentation.command.exception.InvalidImageException;
import com.benchpress200.photique.singlework.presentation.validator.annotation.Enum;
import com.benchpress200.photique.tag.presentation.validator.annotation.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
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
    @NotBlank(message = "Title must not be null")
    @Size(min = 1, max = 30, message = "Invalid title")
    private String title;

    @NotBlank(message = "Description must not be null")
    @Size(min = 1, max = 500, message = "Invalid description")
    private String description;

    @NotBlank(message = "Camera must not be null")
    @Size(min = 1, max = 30, message = "Invalid camera")
    private String camera;

    @Size(max = 30, message = "Invalid lens")
    private String lens;

    @Enum(enumClass = Aperture.class, message = "Invalid aperture")
    private String aperture;

    @Enum(enumClass = ShutterSpeed.class, message = "Invalid shutter speed")
    private String shutterSpeed;

    @Enum(enumClass = ISO.class, message = "Invalid ISO")
    private String iso;

    @NotBlank(message = "Invalid category")
    @Enum(enumClass = Category.class, message = "Invalid category")
    private String category;

    @Size(max = 30, message = "Invalid location")
    private String location;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Tag
    private List<String> tags;

    public SingleWorkCreateCommand toCommand(MultipartFile image) {
        // 이미지 파일 검증 로직
        if (!ImageValidator.isValid(image)) {
            throw new InvalidImageException();
        }

        // null 값이라면 NPE 방지를 위한 빈 리스트 할당
        if (tags == null) {
            tags = new ArrayList<>();
        }

        return SingleWorkCreateCommand.builder()
                .title(title)
                .description(description)
                .image(image)
                .camera(camera)
                .lens(lens)
                .aperture(Aperture.from(aperture))
                .shutterSpeed(ShutterSpeed.from(shutterSpeed))
                .iso(ISO.from(iso))
                .category(Category.from(category))
                .location(location)
                .date(date)
                .tags(tags)
                .build();
    }
}
