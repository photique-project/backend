package com.benchpress200.photique.exhibition.infrastructure.persistence.adapter;

import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionCommentCommandPort;
import com.benchpress200.photique.exhibition.application.query.port.out.ExhibitionCommentQueryPort;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import com.benchpress200.photique.exhibition.infrastructure.persistence.jpa.ExhibitionCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ExhibitionCommentPersistenceAdapter implements
        ExhibitionCommentQueryPort,
        ExhibitionCommentCommandPort {

    private final ExhibitionCommentRepository exhibitionCommentRepository;

    @Override
    public ExhibitionComment save(ExhibitionComment exhibitionComment) {
        return exhibitionCommentRepository.save(exhibitionComment);
    }
}
