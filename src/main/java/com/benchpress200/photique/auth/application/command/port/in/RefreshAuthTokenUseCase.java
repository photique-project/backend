package com.benchpress200.photique.auth.application.command.port.in;

import com.benchpress200.photique.auth.application.command.model.AuthTokenRefreshCommand;
import com.benchpress200.photique.auth.application.command.result.AuthTokenResult;

public interface RefreshAuthTokenUseCase {
    AuthTokenResult refreshAuthToken(AuthTokenRefreshCommand command);
}
