package com.benchpress200.photique.user.domain.support;

import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;

public class FollowFixture {
    private FollowFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private User follower = UserFixture.builder().id(1L).build();
        private User followee = UserFixture.builder().id(2L).build();

        public Builder follower(User follower) {
            this.follower = follower;
            return this;
        }

        public Builder followee(User followee) {
            this.followee = followee;
            return this;
        }

        public Follow build() {
            return Follow.of(follower, followee);
        }
    }
}
