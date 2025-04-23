package com.benchpress200.photique.user.application.cache;

import com.benchpress200.photique.user.domain.dto.UserDetailsRequest;
import com.benchpress200.photique.user.domain.dto.UserDetailsResponse;

public interface UserCacheService {
    UserDetailsResponse getUserDetails(UserDetailsRequest userDetailsRequest);
}
