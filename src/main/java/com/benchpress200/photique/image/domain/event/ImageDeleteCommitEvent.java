package com.benchpress200.photique.image.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageDeleteCommitEvent {
    private String imageUrl;
}
