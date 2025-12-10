package com.benchpress200.photique.exception;


import com.benchpress200.photique.auth.application.exception.EmailAlreadyInUseException;
import com.benchpress200.photique.auth.application.exception.EmailNotFoundException;
import com.benchpress200.photique.auth.application.exception.InvalidRefreshTokenException;
import com.benchpress200.photique.auth.application.exception.VerificationCodeNotFoundException;
import com.benchpress200.photique.auth.domain.exception.MailAuthenticationCodeExpirationException;
import com.benchpress200.photique.auth.domain.exception.MailAuthenticationCodeNotVerifiedException;
import com.benchpress200.photique.auth.exception.LoginRequestObjectReadException;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.image.domain.exception.ImageUploaderFileWriteException;
import com.benchpress200.photique.image.domain.exception.S3DeleteException;
import com.benchpress200.photique.image.domain.exception.S3UploadException;
import com.benchpress200.photique.user.application.exception.DuplicatedFollowException;
import com.benchpress200.photique.user.application.exception.DuplicatedUserException;
import com.benchpress200.photique.user.application.exception.InvalidFollowRequestException;
import com.benchpress200.photique.user.application.exception.UserNotFoundException;
import com.benchpress200.photique.user.presentation.exception.InvalidProfileImageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    // 전달받은 id를 가진 유저 찾지 못했을 때 예외 처리 응답
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(final UserNotFoundException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.NOT_FOUND,
                errorMessage
        );
    }

    // 경로 변수로 전달한 값의 타입 오류 예외 처리 응답
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {

        return ResponseHandler.handleResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid path or parameter variable type"
        );
    }

    // S3 이미지 삭제 예외 처리 응답
    @ExceptionHandler(S3DeleteException.class)
    public ResponseEntity<?> handleS3DeleteException(final S3DeleteException s3DeleteException) {
        String errorMessage = s3DeleteException.getMessage();
        String imageUrl = s3DeleteException.getImageUrl();
        log.error(errorMessage);
        log.error("[{}] delete failed", imageUrl);

        return ResponseHandler.handleResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                SERVER_ERROR_MESSAGE
        );
    }

    // 로그인 필터에 들어오 요청 객체 I/O 예외 처리 응답
    @ExceptionHandler(LoginRequestObjectReadException.class)
    public ResponseEntity<?> handleLoginRequestObjectReadException(final LoginRequestObjectReadException e) {
        String errorMessage = e.getMessage();
        log.error(errorMessage);

        return ResponseHandler.handleResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                SERVER_ERROR_MESSAGE
        );
    }

    // 새 유저 저장 중에 중복된 이메일 또는 닉네임 예외 처리 응답
    @ExceptionHandler(DuplicatedUserException.class)
    public ResponseEntity<?> handleDuplicatedUserException(final DuplicatedUserException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.CONFLICT,
                errorMessage
        );
    }

    // 본인 팔로우 요청 예외 처리 응답
    @ExceptionHandler(InvalidFollowRequestException.class)
    public ResponseEntity<?> handleInvalidFollowRequestException(final InvalidFollowRequestException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.BAD_REQUEST,
                errorMessage
        );
    }

    // 이미 팔로우한 요청 예외 처리 응답
    @ExceptionHandler(DuplicatedFollowException.class)
    public ResponseEntity<?> handleDuplicatedFollowException(final DuplicatedFollowException e) {
        return ResponseHandler.handleResponse(
                HttpStatus.NO_CONTENT
        );
    }

    // 회원가입 인증 메일 요청 시, 이미 가입된 이메일 예외 처리 응답
    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<?> handleEmailAlreadyInUseException(final EmailAlreadyInUseException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.CONFLICT,
                errorMessage
        );
    }

    // 비밀번호 찾기 인증 메일 요청 시, 해당 이메일을 가진 유저가 존재하지 않을 때 예외 처리 응답
    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<?> handleEmailNotFoundException(final EmailNotFoundException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.NOT_FOUND,
                errorMessage
        );
    }

    // 메일 인증 코드 요청 시, 코드가 만료되었을 때 예외 처리 응답
    @ExceptionHandler(VerificationCodeNotFoundException.class)
    public ResponseEntity<?> handleVerificationCodeNotFoundException(final VerificationCodeNotFoundException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.GONE,
                errorMessage
        );
    }

    // 유효하지 않은 리프레쉬 토큰 예외 처리 응답
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<?> handleInvalidRefreshTokenException(final InvalidRefreshTokenException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.UNAUTHORIZED,
                errorMessage
        );
    }
}
