package com.benchpress200.photique.support.fixture;

import org.springframework.mock.web.MockMultipartFile;

public class MultipartFileFixture {
    private MultipartFileFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String key;
        private String fileName;
        private String contentType;
        private byte[] content;

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder content(byte[] content) {
            this.content = content;
            return this;
        }

        public MockMultipartFile build() {
            return new MockMultipartFile(
                    key,
                    fileName,
                    contentType,
                    content
            );
        }
    }
}
