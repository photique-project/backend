package com.benchpress200.photique.tag.domain.dto;

import com.benchpress200.photique.tag.domain.entity.Tag;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NewTagRequest {
    @Size(max = 10, message = "Each tag must not exceed 10 characters")
    private String name;

    public Tag toEntity() {
        return Tag.builder()
                .name(name)
                .build();
    }
}
