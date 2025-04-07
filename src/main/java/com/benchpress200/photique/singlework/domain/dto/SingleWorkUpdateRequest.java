package com.benchpress200.photique.singlework.domain.dto;

import com.benchpress200.photique.singlework.domain.enumeration.Aperture;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.ISO;
import com.benchpress200.photique.singlework.domain.enumeration.ShutterSpeed;
import com.benchpress200.photique.singlework.validation.annotation.Enum;
import com.benchpress200.photique.singlework.validation.annotation.Image;
import com.benchpress200.photique.tag.domain.dto.NewTagRequest;
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
public class SingleWorkUpdateRequest {

    private Long id;

    @Image
    private MultipartFile image; // 업데이트에서 null 이면 수정하지 않는 요청

    @NotNull
    @Size(min = 1, max = 30, message = "Invalid camera: 1 ~ 30 characters")
    private String camera;

    @Size(max = 30, message = "Invalid lens: 1 ~ 30 characters")
    private String lens;

    @Enum(enumClass = Aperture.class, message = "Invalid aperture")
    private String aperture;

    @Enum(enumClass = ShutterSpeed.class, message = "Invalid shutter Speed")
    private String shutterSpeed;

    @Enum(enumClass = ISO.class, message = "Invalid ISO")
    private String iso;

    @Size(max = 30, message = "Invalid location: maximum 30 characters")
    private String location;

    @NotNull(message = "Invalid category: category must not be null")
    @Enum(enumClass = Category.class, message = "Invalid category")
    private String category;

    @NotNull(message = "Invalid Date: date must not be blank.")
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDate date;

    @Size(max = 5, message = "Invalid Tags: the number of tag must be between 0 and 5")
    @Valid
    private List<NewTagRequest> tags;

    @NotBlank(message = "Invalid title: title must not be blank")
    @Size(min = 1, max = 30, message = "Invalid title: 1 ~ 30 characters")
    private String title;

    @NotBlank(message = "Invalid description: description must not be blank.")
    @Size(min = 1, max = 500, message = "Invalid description: 1 ~ 30 characters")
    private String description;

    public List<String> getTags() {
        return tags.stream().map(NewTagRequest::getName).toList();
    }

    public void withSingleWorkId(Long id) {
        this.id = id;
    }
}
