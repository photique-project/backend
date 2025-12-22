package com.benchpress200.photique.singlework.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateSingleWorkSearchEvent {
    private Long singleWorkId;
}
