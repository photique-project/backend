package com.benchpress200.photique.exhibition.infrastructure.persistence.adapter;

import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionWorkCommandPort;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionWork;
import com.benchpress200.photique.exhibition.infrastructure.persistence.jpa.ExhibitionWorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ExhibitionWorkPersistenceAdapter implements
        ExhibitionWorkCommandPort {
    private final ExhibitionWorkRepository exhibitionWorkRepository;

    @Override
    public ExhibitionWork save(ExhibitionWork exhibitionWork) {
        return exhibitionWorkRepository.save(exhibitionWork);
    }
}
