package com.benchpress200.photique.image.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageUploadRollbackEvent {
    private String imageUrl;
}
