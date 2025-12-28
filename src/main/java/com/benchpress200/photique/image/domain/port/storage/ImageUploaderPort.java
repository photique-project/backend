package com.benchpress200.photique.image.domain.port.storage;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploaderPort {
    String upload(MultipartFile image, String path);

    String update(MultipartFile newImage, String oldPath, String newPath);

    void delete(String path);
}
