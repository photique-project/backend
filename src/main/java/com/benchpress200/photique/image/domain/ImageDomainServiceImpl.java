package com.benchpress200.photique.image.domain;

import com.benchpress200.photique.common.transaction.rollbackcontext.ImageRollbackContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageDomainServiceImpl implements ImageDomainService {
    private final ImageUploader imageUploader;

    @Override
    public String upload(final MultipartFile image, final String path) {
        if (image != null) {
            if (image.isEmpty()) { // 빈 객체일 경우 기본값 설정 요청
                return null;
            }

            // 업로드
            String uploadedImageUrl = imageUploader.upload(image, path);

            // 예외발생 시 롤백을 위한 이미지 추가
            ImageRollbackContext.addUploadedImage(uploadedImageUrl);

            return uploadedImageUrl;
        }

        return null;
    }

    @Override
    public String update(
            final MultipartFile newImage,
            final String oldPath,
            final String newPath
    ) {
        // 새 이미지가 null 이라면 수정 X
        if (newImage == null) {
            return oldPath;
        }

        // 기본값 설정이라면
        if (newImage.isEmpty()) {
            return null;
        }

        delete(oldPath);
        return upload(newImage, newPath);
    }

    @Override
    public void delete(final String path) {
        if (path != null) {
            imageUploader.delete(path);
        }
    }
}
