package com.benchpress200.photique.singlework.presentation.request;

import com.benchpress200.photique.singlework.application.command.UpdateSingleWorkCommand;
import com.benchpress200.photique.singlework.domain.enumeration.Aperture;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.ISO;
import com.benchpress200.photique.singlework.domain.enumeration.ShutterSpeed;
import com.benchpress200.photique.singlework.presentation.exception.InvalidFieldToUpdateException;
import com.benchpress200.photique.singlework.presentation.validator.annotation.Enum;
import com.benchpress200.photique.tag.presentation.validator.annotation.Tag;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateSingleWorkRequest {
    private static final String INVALID_TITLE = "Invalid title";
    private static final String INVALID_DESCRIPTION = "Invalid description";
    private static final String INVALID_CAMERA = "Invalid camera";
    private static final String INVALID_LENS = "Invalid lens";
    private static final String INVALID_APERTURE = "Invalid aperture";
    private static final String INVALID_SHUTTER_SPEED = "Invalid shutter speed";
    private static final String INVALID_ISO = "Invalid ISO";
    private static final String INVALID_CATEGORY = "Invalid category";
    private static final String INVALID_LOCATION = "Invalid location";
    private static final String INVALID_DATE = "Invalid date";

    @Size(min = 1, max = 30, message = INVALID_TITLE)
    private String title;
    private boolean updateTitle;

    @Size(min = 1, max = 500, message = INVALID_DESCRIPTION)
    private String description;
    private boolean updateDescription;

    @Size(min = 1, max = 30, message = INVALID_CAMERA)
    private String camera;
    private boolean updateCamera;

    @Size(max = 30, message = INVALID_LENS)
    private String lens;
    private boolean updateLens;

    @Enum(enumClass = Aperture.class, message = INVALID_APERTURE)
    private String aperture;
    private boolean updateAperture;

    @Enum(enumClass = ShutterSpeed.class, message = INVALID_SHUTTER_SPEED)
    private String shutterSpeed;
    private boolean updateShutterSpeed;

    @Enum(enumClass = ISO.class, message = INVALID_ISO)
    private String iso;
    private boolean updateIso;

    @Enum(enumClass = Category.class, message = INVALID_CATEGORY)
    private String category;
    private boolean updateCategory;

    @Size(max = 30, message = INVALID_LOCATION)
    private String location;
    private boolean updateLocation;

    private LocalDate date;
    private boolean updateDate;

    @Tag
    private List<String> tags;
    private boolean updateTags;

    public UpdateSingleWorkCommand toCommand(Long singleWorkId) {
        // 제목(null 허용 X)이 업데이트 대상인데 null로 업데이트할 경우
        if (updateTitle && title == null) {
            throw new InvalidFieldToUpdateException(INVALID_TITLE);
        }

        // 설명(null 허용 X) 업데이트 대상인데 null로 업데이트할 경우
        if (updateDescription && description == null) {
            throw new InvalidFieldToUpdateException(INVALID_DESCRIPTION);
        }

        // 카메라(null 허용 X) 업데이트 대상인데 null로 업데이트할 경우
        if (updateCamera && camera == null) {
            throw new InvalidFieldToUpdateException(INVALID_CAMERA);
        }

        // 카테고리(null 허용 X) 업데이트 대상인데 null로 업데이트할 경우
        if (updateCategory && category == null) {
            throw new InvalidFieldToUpdateException(INVALID_CATEGORY);
        }

        // 태그를 빈 값으로 업데이트할 경우 NPE 방지를 위한 빈 리스트 할당
        if (updateTags && tags == null) {
            tags = new ArrayList<>();
        }

        // 날짜(null 허용 X) 업데이트 대상인데 null로 업데이트할 경우
        if (updateDate && date == null) {
            throw new InvalidFieldToUpdateException(INVALID_DATE);
        }

        // 검색데이터(ES 저장) 업데이트 플래그
        boolean update = updateTitle ||
                updateCategory ||
                updateTags;

        return UpdateSingleWorkCommand.builder()
                .singleWorkId(singleWorkId)
                .updateTitle(updateTitle)
                .title(title)
                .updateDescription(updateDescription)
                .description(description)
                .updateCamera(updateCamera)
                .camera(camera)
                .updateLens(updateLens)
                .lens(lens)
                .updateAperture(updateAperture)
                .aperture(Aperture.from(aperture))
                .updateShutterSpeed(updateShutterSpeed)
                .shutterSpeed(ShutterSpeed.from(shutterSpeed))
                .updateIso(updateIso)
                .iso(ISO.from(iso))
                .updateCategory(updateCategory)
                .category(Category.from(category))
                .updateLocation(updateLocation)
                .location(location)
                .updateDate(updateDate)
                .date(date)
                .updateTags(updateTags)
                .tags(tags)
                .update(update)
                .build();
    }
}
