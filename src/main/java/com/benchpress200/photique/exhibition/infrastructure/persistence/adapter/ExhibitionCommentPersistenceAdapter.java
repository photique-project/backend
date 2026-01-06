package com.benchpress200.photique.exhibition.infrastructure.persistence.adapter;

import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionCommentCommandPort;
import com.benchpress200.photique.exhibition.application.query.port.out.ExhibitionCommentQueryPort;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import com.benchpress200.photique.exhibition.infrastructure.persistence.jpa.ExhibitionCommentRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public Page<ExhibitionComment> findByExhibitionIdWithWriter(Long exhibitionId, Pageable pageable) {
        return exhibitionCommentRepository.findByExhibitionIdWithWriter(exhibitionId, pageable);
    }

    @Override
    public Optional<ExhibitionComment> findById(Long id) {
        return exhibitionCommentRepository.findById(id);
    }
}
