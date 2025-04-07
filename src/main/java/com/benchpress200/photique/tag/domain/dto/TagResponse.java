package com.benchpress200.photique.tag.domain.dto;

import com.benchpress200.photique.tag.domain.entity.Tag;
import lombok.Builder;

@Builder
public record TagResponse(String name) {
    public static TagResponse from(final Tag tag) {
        return TagResponse.builder()
                .name(tag.getName())
                .build();
    }

    public static TagResponse from(final String tag) {
        return TagResponse.builder()
                .name(tag)
                .build();
    }
}
