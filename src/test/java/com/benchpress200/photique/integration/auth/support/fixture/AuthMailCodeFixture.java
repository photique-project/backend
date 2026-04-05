package com.benchpress200.photique.integration.auth.support.fixture;

import com.benchpress200.photique.auth.domain.entity.AuthMailCode;
import com.benchpress200.photique.auth.domain.support.AuthCodeGenerator;

public class AuthMailCodeFixture {
    private AuthMailCodeFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String email = "test@example.com";
        private String code = AuthCodeGenerator.generate();
        private boolean isVerified = false;

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder isVerified(boolean isVerified) {
            this.isVerified = isVerified;
            return this;
        }

        public AuthMailCode build() {
            AuthMailCode authMailCode = AuthMailCode.of(email, code);

            if (isVerified) {
                authMailCode.verify();
            }

            return authMailCode;
        }
    }
}
