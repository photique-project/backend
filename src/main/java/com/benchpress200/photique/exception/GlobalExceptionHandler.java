package com.benchpress200.photique.exception;


import com.benchpress200.photique.auth.exception.AuthException;
import com.benchpress200.photique.common.exception.ImageUploaderException;
import com.benchpress200.photique.common.response.ApiFailureResponse;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.user.exception.UserException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiFailureResponse handleDTOException(
            final MethodArgumentNotValidException e,
            final HttpServletRequest request
    ) {
        StringBuilder sb = new StringBuilder();

        e.getBindingResult().getAllErrors().forEach((error)-> {
            sb.append(error.getDefaultMessage()).append(", ");
        });

        if (!sb.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }

        printLog(request, sb.toString());

        return ResponseHandler.handleFailureResponse(sb.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ImageUploaderException.class)
    public ApiFailureResponse handleImageUploaderException(
            final ImageUploaderException e,
            final HttpServletRequest request
    ) {
        if(e.getOriginMessage() != null) {
            log.error(e.getOriginMessage());
        }

        printLog(request, e.getMessage());

        return ResponseHandler.handleFailureResponse(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserException.class)
    public ApiFailureResponse handleUserException(
            final UserException e,
            final HttpServletRequest request
    ) {
        if(e.getOriginMessage() != null) {
            log.error(e.getOriginMessage());
        }

        printLog(request, e.getMessage());

        return ResponseHandler.handleFailureResponse(e.getMessage(), e.getHttpStatus());
    }

    @ExceptionHandler(AuthException.class)
    public ApiFailureResponse handleAuthException(
            final AuthException e,
            final HttpServletRequest request
    ) {
        if(e.getOriginMessage() != null) {
            log.error(e.getOriginMessage());
        }

        printLog(request, e.getMessage());

        if (e.getHttpStatus() == HttpStatus.INTERNAL_SERVER_ERROR) {
            return ResponseHandler.handleFailureResponse(e.getHttpStatus());
        }

        return ResponseHandler.handleFailureResponse(e.getMessage(), e.getHttpStatus());

    }

    private void printLog(HttpServletRequest request, String message) {
        log.error(
                "[Remote Address: {}] [Request URL: {}] [Method: {}] [Messages: {}]",
                request.getRemoteAddr(),
                request.getRequestURL(),
                request.getMethod(),
                message
        );
    }
}
