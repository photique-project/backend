package com.benchpress200.photique.singlework.api.command.support.fixture;

import com.benchpress200.photique.singlework.api.command.request.SingleWorkUpdateRequest;
import java.time.LocalDate;
import java.util.List;

public class SingleWorkUpdateRequestFixture {
    private SingleWorkUpdateRequestFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean updateTitle = true;
        private String title = "수정된 제목";

        private boolean updateDescription = false;
        private String description = null;

        private boolean updateCamera = false;
        private String camera = null;

        private boolean updateLens = false;
        private String lens = null;

        private boolean updateAperture = false;
        private String aperture = null;

        private boolean updateShutterSpeed = false;
        private String shutterSpeed = null;

        private boolean updateIso = false;
        private String iso = null;

        private boolean updateCategory = false;
        private String category = null;

        private boolean updateLocation = false;
        private String location = null;

        private boolean updateDate = false;
        private LocalDate date = null;

        private boolean updateTags = false;
        private List<String> tags = null;

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

        public Builder aperture(String aperture) {
            this.aperture = aperture;
            return this;
        }

        public Builder updateShutterSpeed(boolean updateShutterSpeed) {
            this.updateShutterSpeed = updateShutterSpeed;
            return this;
        }

        public Builder shutterSpeed(String shutterSpeed) {
            this.shutterSpeed = shutterSpeed;
            return this;
        }

        public Builder updateIso(boolean updateIso) {
            this.updateIso = updateIso;
            return this;
        }

        public Builder iso(String iso) {
            this.iso = iso;
            return this;
        }

        public Builder updateCategory(boolean updateCategory) {
            this.updateCategory = updateCategory;
            return this;
        }

        public Builder category(String category) {
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

        public SingleWorkUpdateRequest build() {
            return SingleWorkUpdateRequest.builder()
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
                    .build();
        }
    }
}
