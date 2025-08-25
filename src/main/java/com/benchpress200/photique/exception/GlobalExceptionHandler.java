package com.benchpress200.photique.exception;


import com.benchpress200.photique.auth.domain.exception.MailAuthenticationCodeExpirationException;
import com.benchpress200.photique.auth.domain.exception.MailAuthenticationCodeNotVerifiedException;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.image.domain.exception.ImageUploaderFileWriteException;
import com.benchpress200.photique.image.domain.exception.S3UploadException;
import com.benchpress200.photique.user.presentation.exception.InvalidProfileImageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String SERVER_ERROR_MESSAGE = "Internal Server Error";

    // MySQL/ES/Redis 예외 처리 응답
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> handleDataAccessException(final DataAccessException e) {
        String errorMessage = e.getMessage();
        log.error(errorMessage);

        return ResponseHandler.handleResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                SERVER_ERROR_MESSAGE
        );
    }

    // 유저 프로필 이미지 예외 처리 응답
    @ExceptionHandler(InvalidProfileImageException.class)
    public ResponseEntity<?> handleInvalidProfileImageException(final InvalidProfileImageException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.BAD_REQUEST,
                errorMessage
        );
    }

    // 메일 인증 코드 만료 예외 처리 응답
    @ExceptionHandler(MailAuthenticationCodeExpirationException.class)
    public ResponseEntity<?> handleMailAuthenticationCodeExpirationException(
            final MailAuthenticationCodeExpirationException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.GONE,
                errorMessage
        );
    }

    // 메일 인증 코드 미인증 예외 처리 응답
    @ExceptionHandler(MailAuthenticationCodeNotVerifiedException.class)
    public ResponseEntity<?> handleMailAuthenticationCodeNotVerifiedException(
            final MailAuthenticationCodeNotVerifiedException e
    ) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.UNAUTHORIZED,
                errorMessage
        );
    }

    // 이미지 업로드 I/O 예외 처리 응답
    @ExceptionHandler(ImageUploaderFileWriteException.class)
    public ResponseEntity<?> handleImageUploaderFileWriteException(final ImageUploaderFileWriteException e) {
        String errorMessage = e.getMessage();
        log.error(errorMessage);

        return ResponseHandler.handleResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                SERVER_ERROR_MESSAGE
        );
    }

    // S3 업로드 예외 처리 응답
    @ExceptionHandler(S3UploadException.class)
    public ResponseEntity<?> handleS3UploadException(final S3UploadException e) {
        String errorMessage = e.getMessage();
        log.error(errorMessage);

        return ResponseHandler.handleResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                SERVER_ERROR_MESSAGE
        );
    }

    // 유효하지 않은 DTO 필드 예외 처리 응답
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleDTOIllegalArgumentException(final MethodArgumentNotValidException e) {
        StringBuilder sb = new StringBuilder();

        e.getBindingResult().getAllErrors().forEach((error) -> {
            sb.append(error.getDefaultMessage()).append(", ");
        });

        if (!sb.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }

        return ResponseHandler.handleResponse(
                HttpStatus.BAD_REQUEST,
                sb.toString()
        );
    }
}
