package com.benchpress200.photique.common.exception;

import lombok.Getter;

@Getter
public class ImageUploaderException extends RuntimeException {
    private String originMessage; // 500 상태코드 고정이라서 상태코드 안받음 -> 이후 모든 에러 클래스 공통로직 뽑고 수정?

    public ImageUploaderException(
            final String message,
            final String originMessage
    ) {
        super(message);
        this.originMessage = originMessage;
    }

    public ImageUploaderException(
            final String message
    ) {
        super(message);
    }
}
