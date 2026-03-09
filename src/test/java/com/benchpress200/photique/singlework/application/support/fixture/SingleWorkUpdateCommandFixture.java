package com.benchpress200.photique.singlework.application.support.fixture;

import com.benchpress200.photique.singlework.application.command.model.SingleWorkUpdateCommand;
import com.benchpress200.photique.singlework.domain.enumeration.Aperture;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.ISO;
import com.benchpress200.photique.singlework.domain.enumeration.ShutterSpeed;
import java.time.LocalDate;
import java.util.List;

public class SingleWorkUpdateCommandFixture {
    private SingleWorkUpdateCommandFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long singleWorkId = 1L;

        private boolean updateTitle = true;
        private String title = "수정된 제목";

        private boolean updateDescription = false;
        private String description = "수정된 설명";

        private boolean updateCamera = false;
        private String camera = "Canon R5";

        private boolean updateLens = false;
        private String lens = "50mm";

        private boolean updateAperture = false;
        private Aperture aperture = Aperture.F_1_4;

        private boolean updateShutterSpeed = false;
        private ShutterSpeed shutterSpeed = ShutterSpeed.S_1_125;

        private boolean updateIso = false;
        private ISO iso = ISO.ISO_100;

        private boolean updateCategory = false;
        private Category category = Category.PORTRAIT;

        private boolean updateLocation = false;
        private String location = "부산";

        private boolean updateDate = false;
        private LocalDate date = LocalDate.of(2026, 6, 1);

        private boolean updateTags = false;
        private List<String> tags = List.of("tag1", "tag2");

        private boolean update = true;

        public Builder singleWorkId(Long singleWorkId) {
            this.singleWorkId = singleWorkId;
            return this;
        }

        public Builder updateTitle(boolean updateTitle) {
            this.updateTitle = updateTitle;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder updateDescription(boolean updateDescription) {
            this.updateDescription = updateDescription;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder updateCamera(boolean updateCamera) {
            this.updateCamera = updateCamera;
            return this;
        }

        public Builder camera(String camera) {
            this.camera = camera;
            return this;
        }

        public Builder updateLens(boolean updateLens) {
            this.updateLens = updateLens;
            return this;
        }

        public Builder lens(String lens) {
            this.lens = lens;
            return this;
        }

        public Builder updateAperture(boolean updateAperture) {
            this.updateAperture = updateAperture;
            return this;
        }

        public Builder aperture(Aperture aperture) {
            this.aperture = aperture;
            return this;
        }

        public Builder updateShutterSpeed(boolean updateShutterSpeed) {
            this.updateShutterSpeed = updateShutterSpeed;
            return this;
        }

        public Builder shutterSpeed(ShutterSpeed shutterSpeed) {
            this.shutterSpeed = shutterSpeed;
            return this;
        }

        public Builder updateIso(boolean updateIso) {
            this.updateIso = updateIso;
            return this;
        }

        public Builder iso(ISO iso) {
            this.iso = iso;
            return this;
        }

        public Builder updateCategory(boolean updateCategory) {
            this.updateCategory = updateCategory;
            return this;
        }

        public Builder category(Category category) {
            this.category = category;
            return this;
        }

        public Builder updateLocation(boolean updateLocation) {
            this.updateLocation = updateLocation;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder updateDate(boolean updateDate) {
            this.updateDate = updateDate;
            return this;
        }

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder updateTags(boolean updateTags) {
            this.updateTags = updateTags;
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder update(boolean update) {
            this.update = update;
            return this;
        }

        public SingleWorkUpdateCommand build() {
            return SingleWorkUpdateCommand.builder()
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
                    .aperture(aperture)
                    .updateShutterSpeed(updateShutterSpeed)
                    .shutterSpeed(shutterSpeed)
                    .updateIso(updateIso)
                    .iso(iso)
                    .updateCategory(updateCategory)
                    .category(category)
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
}
