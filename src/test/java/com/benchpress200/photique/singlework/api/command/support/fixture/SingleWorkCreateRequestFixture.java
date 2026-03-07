package com.benchpress200.photique.singlework.api.command.support.fixture;

import com.benchpress200.photique.singlework.api.command.request.SingleWorkCreateRequest;
import java.time.LocalDate;
import java.util.List;

public class SingleWorkCreateRequestFixture {
    private SingleWorkCreateRequestFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title = "기본 제목";
        private String description = "기본 설명";
        private String camera = "Sony A7";
        private String lens = "24-70";
        private String aperture = "f/0.7";
        private String shutterSpeed = "1/1000";
        private String iso = "400";
        private String category = "landscape";
        private String location = "서울";
        private LocalDate date = LocalDate.of(2026, 1, 1);
        private List<String> tags = List.of("tag1", "tag2");

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder camera(String camera) {
            this.camera = camera;
            return this;
        }

        public Builder lens(String lens) {
            this.lens = lens;
            return this;
        }

        public Builder aperture(String aperture) {
            this.aperture = aperture;
            return this;
        }

        public Builder shutterSpeed(String shutterSpeed) {
            this.shutterSpeed = shutterSpeed;
            return this;
        }

        public Builder iso(String iso) {
            this.iso = iso;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public SingleWorkCreateRequest build() {
            return SingleWorkCreateRequest.builder()
                    .title(title)
                    .description(description)
                    .camera(camera)
                    .lens(lens)
                    .aperture(aperture)
                    .shutterSpeed(shutterSpeed)
                    .iso(iso)
                    .category(category)
                    .location(location)
                    .date(date)
                    .tags(tags)
                    .build();
        }
    }
}
