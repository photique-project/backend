package com.benchpress200.photique.singlework.application.command;

import com.benchpress200.photique.singlework.domain.enumeration.Aperture;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.ISO;
import com.benchpress200.photique.singlework.domain.enumeration.ShutterSpeed;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UpdateSingleWorkCommand {
    private Long singleWorkId;

    private boolean updateTitle;
    private String title;

    private boolean updateDescription;
    private String description;

    private boolean updateCamera;
    private String camera;

    private boolean updateLens;
    private String lens;

    private boolean updateAperture;
    private Aperture aperture;

    private boolean updateShutterSpeed;
    private ShutterSpeed shutterSpeed;

    private boolean updateIso;
    private ISO iso;

    private boolean updateCategory;
    private Category category;

    private boolean updateLocation;
    private String location;

    private boolean updateDate;
    private LocalDate date;

    private boolean updateTags;
    private List<String> tags;

    private boolean update;
}
