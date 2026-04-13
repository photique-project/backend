package com.benchpress200.photique.exhibition.application.support.fixture;

import com.benchpress200.photique.exhibition.application.command.model.ExhibitionWorkCreateCommand;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

public class ExhibitionWorkCreateCommandFixture {
    private ExhibitionWorkCreateCommandFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer displayOrder = 0;
        private String title = "기본 작품 제목";
        private String description = "기본 작품 설명";
        private MultipartFile image = Mockito.mock(MultipartFile.class);

        public Builder displayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder image(MultipartFile image) {
            this.image = image;
            return this;
        }

        public ExhibitionWorkCreateCommand build() {
            return ExhibitionWorkCreateCommand.builder()
                    .displayOrder(displayOrder)
                    .title(title)
                    .description(description)
                    .image(image)
                    .build();
        }
    }
}
