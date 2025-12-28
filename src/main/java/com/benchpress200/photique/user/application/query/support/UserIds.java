package com.benchpress200.photique.user.application.query.support;

import com.benchpress200.photique.user.domain.entity.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;

public class UserIds {
    private final List<Long> ids;

    private UserIds(List<Long> ids) {
        this.ids = List.copyOf(ids);
    }

    public static UserIds from(Page<User> userPage) {
        List<Long> ids = userPage.stream()
                .map(User::getId)
                .toList();

        return new UserIds(ids);
    }

    public List<Long> values() {
        return new ArrayList<>(ids);
    }
}
