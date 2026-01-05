package com.benchpress200.photique.singlework.api.command.request;

import com.benchpress200.photique.common.api.validator.annotation.Enum;
import com.benchpress200.photique.image.presentation.validator.ImageValidator;
import com.benchpress200.photique.singlework.api.command.exception.InvalidImageException;
import com.benchpress200.photique.singlework.application.command.model.SingleWorkCreateCommand;
import com.benchpress200.photique.singlework.domain.enumeration.Aperture;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.ISO;
import com.benchpress200.photique.singlework.domain.enumeration.ShutterSpeed;
import com.benchpress200.photique.tag.api.validator.annotation.Tag;
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
    private static final String INVALID_TITLE = "Invalid title";
    private static final String INVALID_DESCRIPTION = "Invalid description";
    private static final String INVALID_CAMERA = "Invalid camera";
    private static final String INVALID_LENS = "Invalid lens";
    private static final String INVALID_APERTURE = "Invalid aperture";
    private static final String INVALID_SHUTTER_SPEED = "Invalid shutter speed";
    private static final String INVALID_ISO = "Invalid ISO";
    private static final String INVALID_CATEGORY = "Invalid category";
    private static final String INVALID_LOCATION = "Invalid location";

    @NotBlank(message = INVALID_TITLE)
    @Size(min = 1, max = 30, message = INVALID_TITLE)
    private String title;

    @NotBlank(message = INVALID_DESCRIPTION)
    @Size(min = 1, max = 500, message = INVALID_DESCRIPTION)
    private String description;

    @NotBlank(message = INVALID_CAMERA)
    @Size(min = 1, max = 30, message = INVALID_CAMERA)
    private String camera;

    @Size(max = 30, message = INVALID_LENS)
    private String lens;

    @Enum(enumClass = Aperture.class, message = INVALID_APERTURE)
    private String aperture;

    @Enum(enumClass = ShutterSpeed.class, message = INVALID_SHUTTER_SPEED)
    private String shutterSpeed;

    @Enum(enumClass = ISO.class, message = INVALID_ISO)
    private String iso;

    @NotBlank(message = INVALID_CATEGORY)
    @Enum(enumClass = Category.class, message = INVALID_CATEGORY)
    private String category;

    @Size(max = 30, message = INVALID_LOCATION)
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
