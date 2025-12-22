package com.benchpress200.photique.singlework.application.command;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.enumeration.Aperture;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.ISO;
import com.benchpress200.photique.singlework.domain.enumeration.ShutterSpeed;
import com.benchpress200.photique.user.domain.entity.User;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class NewSingleWorkCommand {
    private String title;
    private String description;
    private MultipartFile image;
    private String camera;
    private String lens;
    private Aperture aperture;
    private ShutterSpeed shutterSpeed;
    private ISO iso;
    private Category category;
    private String location;
    private LocalDate date;
    private List<String> tags;

    public SingleWork toEntity(
            User writer,
            String imageUrl
    ) {
        return SingleWork.builder()
                .title(title)
                .description(description)
                .writer(writer)
                .image(imageUrl)
                .camera(camera)
                .lens(lens)
                .aperture(aperture)
                .shutterSpeed(shutterSpeed)
                .iso(iso)
                .location(location)
                .category(category)
                .date(date)
                .build();
    }
}
