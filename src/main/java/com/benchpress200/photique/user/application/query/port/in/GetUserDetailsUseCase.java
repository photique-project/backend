package com.benchpress200.photique.user.application.query.port.in;

import com.benchpress200.photique.user.application.query.result.UserDetailsResult;

public interface GetUserDetailsUseCase {
    UserDetailsResult getUserDetails(Long userId);
}
