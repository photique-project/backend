package com.benchpress200.photique.common.infrastructure;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface ImageUploader {
    String upload(MultipartFile image, String path) ;
    String update(MultipartFile newImage, String oldPath, String newPath) throws IOException;
    void delete(String path);
}
