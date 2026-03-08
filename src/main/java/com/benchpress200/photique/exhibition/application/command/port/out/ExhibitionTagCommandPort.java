package com.benchpress200.photique.exhibition.application.command.port.out;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionTag;
import java.util.List;

public interface ExhibitionTagCommandPort {
    ExhibitionTag save(ExhibitionTag exhibitionTag);

    List<ExhibitionTag> saveAll(List<ExhibitionTag> exhibitionTags);

    void deleteByExhibition(Exhibition exhibition);
}
