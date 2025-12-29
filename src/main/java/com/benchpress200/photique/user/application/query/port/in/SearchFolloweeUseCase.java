package com.benchpress200.photique.user.application.query.port.in;

import com.benchpress200.photique.user.application.query.model.FolloweeSearchQuery;
import com.benchpress200.photique.user.application.query.result.FolloweeSearchResult;

public interface SearchFolloweeUseCase {
    FolloweeSearchResult searchFollowee(FolloweeSearchQuery query);
}
