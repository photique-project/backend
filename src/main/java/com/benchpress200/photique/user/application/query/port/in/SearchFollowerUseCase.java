package com.benchpress200.photique.user.application.query.port.in;

import com.benchpress200.photique.user.application.query.model.FollowerSearchQuery;
import com.benchpress200.photique.user.application.query.result.FollowerSearchResult;

public interface SearchFollowerUseCase {
    FollowerSearchResult searchFollower(FollowerSearchQuery query);
}
