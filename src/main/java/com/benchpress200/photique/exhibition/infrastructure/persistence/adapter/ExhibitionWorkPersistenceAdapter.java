package com.benchpress200.photique.exhibition.infrastructure.persistence.adapter;

import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionWorkCommandPort;
import com.benchpress200.photique.exhibition.application.query.port.out.ExhibitionWorkQueryPort;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionWork;
import com.benchpress200.photique.exhibition.infrastructure.persistence.jpa.ExhibitionWorkRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ExhibitionWorkPersistenceAdapter implements
        ExhibitionWorkCommandPort,
        ExhibitionWorkQueryPort {
    private final ExhibitionWorkRepository exhibitionWorkRepository;


    @Override
    public ExhibitionWork save(ExhibitionWork exhibitionWork) {
        return exhibitionWorkRepository.save(exhibitionWork);
    }

    @Override
    public Optional<ExhibitionWork> findById(Long id) {
        return exhibitionWorkRepository.findById(id);
    }

    @Override
    public List<ExhibitionWork> findByExhibition(Exhibition exhibition) {
        return exhibitionWorkRepository.findByExhibition(exhibition);
    }
}
