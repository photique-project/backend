package com.benchpress200.photique.image.infrastructure.storage.adapter;

import com.benchpress200.photique.image.domain.port.storage.ImageUploaderPort;
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
        return s3ImageUploader.upload(image, path);
    }

    @Override
    public String update(MultipartFile newImage, String oldPath, String newPath) {
        return s3ImageUploader.update(newImage, oldPath, newPath);
    }

    @Override
    public void delete(String path) {
        s3ImageUploader.delete(path);
    }
}
