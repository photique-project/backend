package com.benchpress200.photique.image.domain;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploader {
    String upload(MultipartFile image, String path);

    String update(MultipartFile newImage, String oldPath, String newPath);

    void delete(String path);
}
