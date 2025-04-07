package com.benchpress200.photique.user.validation.validator;

import com.benchpress200.photique.user.validation.annotation.ProfileImage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ProfileImageValidator implements ConstraintValidator<ProfileImage, MultipartFile> {
    @Override
    public void initialize(ProfileImage constraintAnnotation) {
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        // null 가능
        if (file == null) {
            return true;
        }

        // 빈 파일 불가능
        if (file.isEmpty()) {
            return false;
        }

        // 사이즈 5MB 이하
        if (file.getSize() > 5 * 1024 * 1024) {
            return false;
        }

        String filename = file.getOriginalFilename();
        return filename != null && (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(
                ".png"));
    }
}
