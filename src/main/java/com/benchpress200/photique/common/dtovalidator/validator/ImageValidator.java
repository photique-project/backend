package com.benchpress200.photique.common.dtovalidator.validator;

import com.benchpress200.photique.common.dtovalidator.annotation.Image;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ImageValidator implements ConstraintValidator<Image, MultipartFile> {

    @Override
    public void initialize(Image constraintAnnotation) {
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true;
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            return false;
        }

        String filename = file.getOriginalFilename();
        return filename != null && (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(
                ".png"));
    }
}
