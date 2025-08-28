package com.benchpress200.photique.common.response;

import java.time.LocalDateTime;

public record ResponseBody<T>(
        int status,
        String message,
        T data,
        LocalDateTime timestamp
) {
}
