package com.benchpress200.photique.exhibition.infrastructure.persistence.adapter;

import com.benchpress200.photique.exhibition.application.query.port.out.ExhibitionQueryPort;
import com.benchpress200.photique.exhibition.infrastructure.persistence.jpa.ExhibitionRepository;
import com.benchpress200.photique.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ExhibitionPersistenceAdapter implements
        ExhibitionQueryPort {

    private final ExhibitionRepository exhibitionRepository;

    @Override
    public Long countByWriter(User writer) {
        return exhibitionRepository.countByWriter(writer);
    }
}
