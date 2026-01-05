package com.benchpress200.photique.exhibition.infrastructure.persistence.adapter;

import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionCommandPort;
import com.benchpress200.photique.exhibition.application.query.port.out.ExhibitionQueryPort;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import com.benchpress200.photique.exhibition.domain.enumeration.Target;
import com.benchpress200.photique.exhibition.infrastructure.persistence.elasticsearch.ExhibitionSearchRepository;
import com.benchpress200.photique.exhibition.infrastructure.persistence.jpa.ExhibitionRepository;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ExhibitionPersistenceAdapter implements
        ExhibitionQueryPort,
        ExhibitionCommandPort {

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionSearchRepository exhibitionSearchRepository;

    @Override
    public Long countByWriter(User writer) {
        return exhibitionRepository.countByWriter(writer);
    }

    @Override
    public Optional<Exhibition> findActiveById(Long id) {
        return exhibitionRepository.findActiveById(id);
    }

    @Override
    public Optional<Exhibition> findActiveByIdWithWriter(Long id) {
        return exhibitionRepository.findActiveByIdWithWriter(id);
    }

    @Override
    public Page<ExhibitionSearch> search(Target target, String keyword, Pageable pageable) {
        return exhibitionSearchRepository.search(
                target,
                keyword,
                pageable
        );
    }

    @Override
    public Exhibition save(Exhibition exhibition) {
        return exhibitionRepository.save(exhibition);
    }

    @Override
    public void incrementViewCount(Long exhibitionId) {
        exhibitionRepository.incrementViewCount(exhibitionId);
    }

    @Override
    public void incrementLikeCount(Long exhibitionId) {
        exhibitionRepository.incrementLikeCount(exhibitionId);
    }
}
