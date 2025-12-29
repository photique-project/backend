package com.benchpress200.photique.image.infrastructure.storage.adapter;

import com.benchpress200.photique.image.domain.port.storage.ImageUploaderPort;
import com.benchpress200.photique.image.infrastructure.exception.ImageDeleteException;
import com.benchpress200.photique.image.infrastructure.exception.ImageUploadException;
import com.benchpress200.photique.image.infrastructure.storage.s3.S3ImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class ImageUploaderAdapter implements ImageUploaderPort {
    private final S3ImageUploader s3ImageUploader;

    @Override
    public String upload(MultipartFile image, String path) {
        try {
            return s3ImageUploader.upload(image, path);
        } catch (RuntimeException e) {
            // 이미지 업로드 처리 실패 시 전역 예외 핸들러에서 로깅
            String message = e.getMessage();
            throw new ImageUploadException(message);
        }
    }

    @Override
    public String update(MultipartFile newImage, String oldPath, String newPath) {
        return s3ImageUploader.update(newImage, oldPath, newPath);
    }

    @Override
    public void delete(String path) {
        try {
            s3ImageUploader.delete(path);
        } catch (RuntimeException e) {
            // 이미지 삭제 처리 실패 시 전역 예외 핸들러에서 로깅
            String message = e.getMessage();
            throw new ImageDeleteException(message, path);
        }
    }
}
