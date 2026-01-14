package com.benchpress200.photique.exhibition.application.query.port.out.persistence;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionTag;
import java.util.List;

public interface ExhibitionTagQueryPort {
    List<ExhibitionTag> findByExhibitionWithTag(Exhibition exhibition);
}
