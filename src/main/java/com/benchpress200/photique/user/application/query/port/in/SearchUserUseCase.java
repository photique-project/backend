package com.benchpress200.photique.user.application.query.port.in;

import com.benchpress200.photique.user.application.query.model.UserSearchQuery;
import com.benchpress200.photique.user.application.query.result.UserSearchResult;

public interface SearchUserUseCase {
    UserSearchResult searchUser(UserSearchQuery query);
}
