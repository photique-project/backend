package com.benchpress200.photique.image.infrastructure.storage.adapter;

import com.benchpress200.photique.image.domain.port.storage.ImageUploaderPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Profile("local")
@Component
public class LocalImageUploaderAdapter implements ImageUploaderPort {
    @Value("${dummy-image-url}")
    private String dummyImageUrl;

    @Override
    public String upload(MultipartFile image, String path) {
        return dummyImageUrl + path;
    }

    @Override
    public String update(MultipartFile newImage, String oldPath, String newPath) {
        return dummyImageUrl + newPath;
    }

    @Override
    public void delete(String path) {
        // no-op
    }
}
