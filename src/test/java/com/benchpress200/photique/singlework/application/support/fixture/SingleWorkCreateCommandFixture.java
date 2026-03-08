package com.benchpress200.photique.singlework.application.support.fixture;

import com.benchpress200.photique.singlework.application.command.model.SingleWorkCreateCommand;
import com.benchpress200.photique.singlework.domain.enumeration.Aperture;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.ISO;
import com.benchpress200.photique.singlework.domain.enumeration.ShutterSpeed;
import java.time.LocalDate;
import java.util.List;

public class SingleWorkCreateCommandFixture {
    private SingleWorkCreateCommandFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title = "기본 제목";
        private String description = "기본 설명";
        private String camera = "Sony A7";
        private String lens = "24-70";
        private Aperture aperture = Aperture.F_0_7;
        private ShutterSpeed shutterSpeed = ShutterSpeed.S_1;
        private ISO iso = ISO.ISO_50;
        private Category category = Category.ETC;
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

        public Builder aperture(Aperture aperture) {
            this.aperture = aperture;
            return this;
        }

        public Builder shutterSpeed(ShutterSpeed shutterSpeed) {
            this.shutterSpeed = shutterSpeed;
            return this;
        }

        public Builder iso(ISO iso) {
            this.iso = iso;
            return this;
        }

        public Builder category(Category category) {
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

        public SingleWorkCreateCommand build() {
            return SingleWorkCreateCommand.builder()
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
