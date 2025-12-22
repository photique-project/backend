package com.benchpress200.photique.image.presentation.validator;

import org.springframework.web.multipart.MultipartFile;

public class ImageValidator {
    private static final long MAX_SIZE = 5 * 1024 * 1024;
    private static final String JPG = ".jpg";
    private static final String JPEG = ".jpeg";
    private static final String PNG = ".png";

    private ImageValidator() {
    }

    public static boolean isValid(MultipartFile file) {
        // null or 빈 파일 불가능
        if (file == null || file.isEmpty()) {
            return false;
        }

        // 사이즈 5MB 이하
        if (file.getSize() > MAX_SIZE) {
            return false;
        }

        String filename = file.getOriginalFilename();

        // 파일 이름 존재 && 확장자 만족
        return filename != null && (
                filename.endsWith(JPG) ||
                        filename.endsWith(JPEG) ||
                        filename.endsWith(PNG)
        );
    }
}
