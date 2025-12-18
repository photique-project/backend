package com.benchpress200.photique.user.presentation.validator;

import org.springframework.web.multipart.MultipartFile;

public class ProfileImageValidator {
    private static final long MAX_SIZE = 5 * 1024 * 1024;
    private static final String JPG = ".jpg";
    private static final String JPEG = ".jpeg";
    private static final String PNG = ".png";

    private ProfileImageValidator() {
    }

    public static boolean isValid(MultipartFile file) {
        // null 가능
        if (file == null) {
            return true;
        }

        // 빈 파일 불가능
        if (file.isEmpty()) {
            return false;
        }

        // 사이즈 5MB 이하
        if (file.getSize() > MAX_SIZE) {
            return false;
        }

        String filename = file.getOriginalFilename();

        return filename != null && (
                filename.endsWith(JPG) ||
                        filename.endsWith(JPEG) ||
                        filename.endsWith(PNG)
        );
    }
}
