package com.benchpress200.photique.exception;


import com.benchpress200.photique.auth.domain.exception.EmailAlreadyInUseException;
import com.benchpress200.photique.auth.domain.exception.EmailNotFoundException;
import com.benchpress200.photique.auth.domain.exception.InvalidRefreshTokenException;
import com.benchpress200.photique.auth.domain.exception.MailAuthenticationCodeExpirationException;
import com.benchpress200.photique.auth.domain.exception.MailAuthenticationCodeNotVerifiedException;
import com.benchpress200.photique.auth.domain.exception.VerificationCodeNotFoundException;
import com.benchpress200.photique.auth.infrastructure.exception.LoginRequestObjectReadException;
import com.benchpress200.photique.auth.infrastructure.exception.MailSendException;
import com.benchpress200.photique.common.api.response.ResponseHandler;
import com.benchpress200.photique.exhibition.api.command.exception.InvalidExhibitionFieldToUpdateException;
import com.benchpress200.photique.exhibition.api.command.exception.InvalidExhibitionImage;
import com.benchpress200.photique.exhibition.api.command.exception.InvalidExhibitionWorkDisplayOrder;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionNotFoundException;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionNotOwnedException;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionWorkDuplicatedDisplayOrderException;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionWorkNotFoundException;
import com.benchpress200.photique.image.infrastructure.exception.ImageDeleteException;
import com.benchpress200.photique.image.infrastructure.exception.ImageUploadException;
import com.benchpress200.photique.image.infrastructure.exception.ImageUploaderFileWriteException;
import com.benchpress200.photique.notification.domain.exception.NotificationTargetSingleWorkNotFoundException;
import com.benchpress200.photique.singlework.api.command.exception.InvalidImageException;
import com.benchpress200.photique.singlework.api.command.exception.InvalidSingleWorkFieldToUpdateException;
import com.benchpress200.photique.singlework.api.query.exception.InvalidFieldToSearch;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkAlreadyLikedException;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkCommentNotFoundException;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkCommentNotOwnedException;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkNotFoundException;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkNotOwnedException;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkWriterNotFoundException;
import com.benchpress200.photique.singlework.infrastructure.exception.ElasticsearchMaxResultWindowException;
import com.benchpress200.photique.singlework.infrastructure.exception.ElasticsearchSearchException;
import com.benchpress200.photique.user.api.command.exception.InvalidProfileImageException;
import com.benchpress200.photique.user.domain.exception.DuplicatedFollowException;
import com.benchpress200.photique.user.domain.exception.DuplicatedUserException;
import com.benchpress200.photique.user.domain.exception.InvalidFollowRequestException;
import com.benchpress200.photique.user.domain.exception.UserNotFoundException;
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
    public ResponseEntity<?> handleDataAccessException(DataAccessException e) {
        String errorMessage = e.getMessage();
        log.error(errorMessage);

        return ResponseHandler.handleResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                SERVER_ERROR_MESSAGE
        );
    }

    // 유저 프로필 이미지 예외 처리 응답
    @ExceptionHandler(InvalidProfileImageException.class)
    public ResponseEntity<?> handleInvalidProfileImageException(InvalidProfileImageException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.BAD_REQUEST,
                errorMessage
        );
    }

    // 메일 인증 코드 만료 예외 처리 응답
    @ExceptionHandler(MailAuthenticationCodeExpirationException.class)
    public ResponseEntity<?> handleMailAuthenticationCodeExpirationException(
            MailAuthenticationCodeExpirationException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.GONE,
                errorMessage
        );
    }

    // 메일 인증 코드 미인증 예외 처리 응답
    @ExceptionHandler(MailAuthenticationCodeNotVerifiedException.class)
    public ResponseEntity<?> handleMailAuthenticationCodeNotVerifiedException(
            MailAuthenticationCodeNotVerifiedException e
    ) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.UNAUTHORIZED,
                errorMessage
        );
    }

    // 이미지 업로드 I/O 예외 처리 응답
    @ExceptionHandler(ImageUploaderFileWriteException.class)
    public ResponseEntity<?> handleImageUploaderFileWriteException(ImageUploaderFileWriteException e) {
        String errorMessage = e.getMessage();
        log.error(errorMessage);

        return ResponseHandler.handleResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                SERVER_ERROR_MESSAGE
        );
    }

    // S3 업로드 예외 처리 응답
    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<?> handleS3UploadException(ImageUploadException e) {
        String errorMessage = e.getMessage();
        log.error(errorMessage);

        return ResponseHandler.handleResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                SERVER_ERROR_MESSAGE
        );
    }

    // 유효하지 않은 DTO 필드 예외 처리 응답
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleDTOIllegalArgumentException(MethodArgumentNotValidException e) {
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
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.NOT_FOUND,
                errorMessage
        );
    }

    // 경로 변수로 전달한 값의 타입 오류 예외 처리 응답
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {

        return ResponseHandler.handleResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid path or parameter variable type"
        );
    }

    // 외부 인프라 이미지 삭제 예외 처리 응답
    @ExceptionHandler(ImageDeleteException.class)
    public ResponseEntity<?> handleImageDeleteException(ImageDeleteException e) {
        String errorMessage = e.getMessage();
        String imageUrl = e.getImageUrl();
        log.error(errorMessage);
        log.error("[{}] delete failed", imageUrl);

        return ResponseHandler.handleResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                SERVER_ERROR_MESSAGE
        );
    }

    // 로그인 필터에 들어오 요청 객체 I/O 예외 처리 응답
    @ExceptionHandler(LoginRequestObjectReadException.class)
    public ResponseEntity<?> handleLoginRequestObjectReadException(LoginRequestObjectReadException e) {
        String errorMessage = e.getMessage();
        log.error(errorMessage);

        return ResponseHandler.handleResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                SERVER_ERROR_MESSAGE
        );
    }

    // 새 유저 저장 중에 중복된 이메일 또는 닉네임 예외 처리 응답
    @ExceptionHandler(DuplicatedUserException.class)
    public ResponseEntity<?> handleDuplicatedUserException(DuplicatedUserException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.CONFLICT,
                errorMessage
        );
    }

    // 본인 팔로우 요청 예외 처리 응답
    @ExceptionHandler(InvalidFollowRequestException.class)
    public ResponseEntity<?> handleInvalidFollowRequestException(InvalidFollowRequestException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.BAD_REQUEST,
                errorMessage
        );
    }

    // 이미 팔로우한 요청 예외 처리 응답
    @ExceptionHandler(DuplicatedFollowException.class)
    public ResponseEntity<?> handleDuplicatedFollowException(DuplicatedFollowException e) {
        return ResponseHandler.handleResponse(
                HttpStatus.NO_CONTENT
        );
    }

    // 회원가입 인증 메일 요청 시, 이미 가입된 이메일 예외 처리 응답
    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<?> handleEmailAlreadyInUseException(EmailAlreadyInUseException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.CONFLICT,
                errorMessage
        );
    }

    // 비밀번호 찾기 인증 메일 요청 시, 해당 이메일을 가진 유저가 존재하지 않을 때 예외 처리 응답
    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<?> handleEmailNotFoundException(EmailNotFoundException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.NOT_FOUND,
                errorMessage
        );
    }

    // 메일 인증 코드 요청 시, 코드가 만료되었을 때 예외 처리 응답
    @ExceptionHandler(VerificationCodeNotFoundException.class)
    public ResponseEntity<?> handleVerificationCodeNotFoundException(VerificationCodeNotFoundException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.GONE,
                errorMessage
        );
    }

    // 유효하지 않은 리프레쉬 토큰 예외 처리 응답
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<?> handleInvalidRefreshTokenException(InvalidRefreshTokenException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.UNAUTHORIZED,
                errorMessage
        );
    }

    // 게시글 유효하지 않은 이미지 예외 처리 응답
    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity<?> handleInvalidImageException(InvalidImageException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.BAD_REQUEST,
                errorMessage
        );
    }

    // 존재하지 않는 단일작품의 작가 예외 처리 응답
    @ExceptionHandler(SingleWorkWriterNotFoundException.class)
    public ResponseEntity<?> handleWriterNotFoundException(SingleWorkWriterNotFoundException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.NOT_FOUND,
                errorMessage
        );
    }

    // 존재하지 않는 단일작품 예외 처리 응답
    @ExceptionHandler(SingleWorkNotFoundException.class)
    public ResponseEntity<?> handleSingleWorkNotFoundException(SingleWorkNotFoundException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.NOT_FOUND,
                errorMessage
        );
    }

    // 존재하지 않는 알림 대상 단일작품 예외 처리 응답
    @ExceptionHandler(NotificationTargetSingleWorkNotFoundException.class)
    public ResponseEntity<?> handleNotificationTargetSingleWorkNotFoundException(
            NotificationTargetSingleWorkNotFoundException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.NOT_FOUND,
                errorMessage
        );
    }

    // 단일작품 업데이트 요청 시, 유효하지 않은 필드 예외 처리 응답
    @ExceptionHandler(InvalidSingleWorkFieldToUpdateException.class)
    public ResponseEntity<?> handleInvalidFieldToUpdateException(InvalidSingleWorkFieldToUpdateException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.BAD_REQUEST,
                errorMessage
        );
    }

    // 단일작품 업데이트 요청 시, 단일작품의 주인이 아닌 유저가 요청했을 때 예외 처리 응답
    @ExceptionHandler(SingleWorkNotOwnedException.class)
    public ResponseEntity<?> handleSingleWorkNotOwnedException(SingleWorkNotOwnedException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.FORBIDDEN,
                errorMessage
        );
    }

    // 단일작품 검색 요청 시, 유효하지 않은 필드 예외 처리 응답
    @ExceptionHandler(InvalidFieldToSearch.class)
    public ResponseEntity<?> handleInvalidFieldToSearchException(InvalidFieldToSearch e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.BAD_REQUEST,
                errorMessage
        );
    }

    // ES 검색 API 동작 예외 처리 응답
    @ExceptionHandler(ElasticsearchSearchException.class)
    public ResponseEntity<?> handleElasticsearchSearchException(ElasticsearchSearchException e) {
        String errorMessage = e.getMessage();
        log.error(errorMessage);

        return ResponseHandler.handleResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                SERVER_ERROR_MESSAGE
        );
    }

    // ES 검색 페이지 초과 예외 처리 응답
    @ExceptionHandler(ElasticsearchMaxResultWindowException.class)
    public ResponseEntity<?> handleElasticsearchMaxResultWindowException(ElasticsearchMaxResultWindowException e) {
        String errorMessage = e.getMessage();
        log.error(errorMessage);

        return ResponseHandler.handleResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                SERVER_ERROR_MESSAGE
        );
    }

    // 메일 전송 예외 처리 응답
    @ExceptionHandler(MailSendException.class)
    public ResponseEntity<?> handleMailSendException(MailSendException e) {
        String errorMessage = e.getMessage();
        log.error(errorMessage);

        return ResponseHandler.handleResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                SERVER_ERROR_MESSAGE
        );
    }

    // 중복 좋아요 예외 처리 응답
    @ExceptionHandler(SingleWorkAlreadyLikedException.class)
    public ResponseEntity<?> handleSingleWorkAlreadyLikedException(SingleWorkAlreadyLikedException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.CONFLICT,
                errorMessage
        );
    }

    // 존재하지 않는 댓글 조회 예외 처리 응답
    @ExceptionHandler(SingleWorkCommentNotFoundException.class)
    public ResponseEntity<?> handleSingleWorkCommentNotFoundException(SingleWorkCommentNotFoundException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.NOT_FOUND,
                errorMessage
        );
    }

    // 단일작품 댓글의 주인이 아닌 유저가 해당 댓글 쓰기 요청했을 때 예외 처리 응답
    @ExceptionHandler(SingleWorkCommentNotOwnedException.class)
    public ResponseEntity<?> handleSingleWorkCommentNotOwnedException(SingleWorkCommentNotOwnedException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.FORBIDDEN,
                errorMessage
        );
    }

    // 전시회 생성 시 유효하지 않은 이미지 예외 처리 응답
    @ExceptionHandler(InvalidExhibitionImage.class)
    public ResponseEntity<?> handleInvalidExhibitionImageException(InvalidExhibitionImage e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.BAD_REQUEST,
                errorMessage
        );
    }

    // 존재하지 않는 전시회 조회 예외 처리 응답
    @ExceptionHandler(ExhibitionNotFoundException.class)
    public ResponseEntity<?> handleExhibitionNotFoundException(ExhibitionNotFoundException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.NOT_FOUND,
                errorMessage
        );
    }

    // 전시회 작품의 중복된 순서 예외 처리 응답
    @ExceptionHandler(InvalidExhibitionWorkDisplayOrder.class)
    public ResponseEntity<?> handleExhibitionWorkDisplayOrderException(InvalidExhibitionWorkDisplayOrder e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.BAD_REQUEST,
                errorMessage
        );
    }

    // 전시회 작품의 유효하지 않은 업데이트 필드 예외 처리 응답
    @ExceptionHandler(InvalidExhibitionFieldToUpdateException.class)
    public ResponseEntity<?> handleInvalidFieldToUpdateException(InvalidExhibitionFieldToUpdateException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.BAD_REQUEST,
                errorMessage
        );
    }

    // 전시회 수정 요청한 유저가 전시회의 주인이 아닐 때 예외 처리 응답
    @ExceptionHandler(ExhibitionNotOwnedException.class)
    public ResponseEntity<?> handleExhibitionNotOwnedException(ExhibitionNotOwnedException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.FORBIDDEN,
                errorMessage
        );
    }

    // 존재하지 않는 전시회 개별 작품 예외 처리 응답
    @ExceptionHandler(ExhibitionWorkNotFoundException.class)
    public ResponseEntity<?> handleExhibitionWorkNotFoundException(ExhibitionWorkNotFoundException e) {
        String errorMessage = e.getMessage();

        return ResponseHandler.handleResponse(
                HttpStatus.NOT_FOUND,
                errorMessage
        );
    }

    @ExceptionHandler(ExhibitionWorkDuplicatedDisplayOrderException.class)
    public ResponseEntity<?> handleExhibitionWorkDuplicatedDisplayOrderException(
            ExhibitionWorkDuplicatedDisplayOrderException e
    ) {
        String errorMessage = e.getMessage();
        return ResponseHandler.handleResponse(
                HttpStatus.BAD_REQUEST,
                errorMessage
        );
    }
}
