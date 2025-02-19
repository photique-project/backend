package com.benchpress200.photique.exhibition.domain.dto;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionWork;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExhibitionDetailResponse {
    private Long id;
    private String title;
    private String description;
    private List<Work> works;


    @Builder
    record Work(
            String image,
            String title,
            String description
    ) {
        public static Work from(
                final ExhibitionWork exhibitionWork
        ) {
            return Work.builder()
                    .image(exhibitionWork.getImage())
                    .title(exhibitionWork.getTitle())
                    .description(exhibitionWork.getDescription())
                    .build();
        }

    }

    public static ExhibitionDetailResponse from(
            final Exhibition exhibition,
            final List<ExhibitionWork> exhibitionWorks
    ) {
        List<Work> works = exhibitionWorks.stream()
                .map(Work::from)
                .toList();

        return ExhibitionDetailResponse.builder()
                .id(exhibition.getId())
                .title(exhibition.getTitle())
                .description(exhibition.getDescription())
                .works(works)
                .build();
    }
}
