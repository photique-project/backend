package com.benchpress200.photique.singlework.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SingleWorkDetailRequest {
    private Long userId;
    private Long singleWorkId;

    public void withSingleWorkId(final Long singleWorkId) {
        this.singleWorkId = singleWorkId;
        
    }
}
