package com.benchpress200.photique.exhibition.infrastructure.persistence.adapter;

import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionTagCommandPort;
import com.benchpress200.photique.exhibition.application.query.port.out.ExhibitionTagQueryPort;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionTag;
import com.benchpress200.photique.exhibition.infrastructure.persistence.jpa.ExhibitionTagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ExhibitionTagPersistenceAdapter implements
        ExhibitionTagCommandPort,
        ExhibitionTagQueryPort {

    private final ExhibitionTagRepository exhibitionTagRepository;


    @Override
    public ExhibitionTag save(ExhibitionTag exhibitionTag) {
        return exhibitionTagRepository.save(exhibitionTag);
    }

    @Override
    public List<ExhibitionTag> findByExhibitionWithTag(Exhibition exhibition) {
        return exhibitionTagRepository.findByExhibitionWithTag(exhibition);
    }
}
