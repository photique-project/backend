package com.benchpress200.photique.singlework.infrastructure.persistence.adapter;

import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkCommandPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkQueryPort;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import com.benchpress200.photique.singlework.infrastructure.persistence.elasticsearch.SingleWorkSearchRepository;
import com.benchpress200.photique.singlework.infrastructure.persistence.jpa.SingleWorkRepository;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SingleWorkPersistenceAdapter implements
        SingleWorkCommandPort,
        SingleWorkQueryPort {
    private final SingleWorkRepository singleWorkRepository;
    private final SingleWorkSearchRepository singleWorkSearchRepository;

    @Override
    public SingleWork save(SingleWork singleWork) {
        return singleWorkRepository.save(singleWork);
    }

    @Override
    public void incrementViewCount(Long singleWorkId) {
        singleWorkRepository.incrementViewCount(singleWorkId);
    }

    @Override
    public Optional<SingleWork> findByIdWithWriter(Long id) {
        return singleWorkRepository.findByIdWithWriter(id);
    }

    @Override
    public Optional<SingleWork> findActiveByIdWithWriter(Long id) {
        return singleWorkRepository.findActiveByIdWithWriter(id);
    }

    @Override
    public Long countByWriter(User writer) {
        return singleWorkRepository.countByWriter(writer);
    }

    @Override
    public Page<SingleWorkSearch> search(
            Target target,
            String keyword,
            List<Category> categories,
            Pageable pageable
    ) {
        return singleWorkSearchRepository.search(
                target,
                keyword,
                categories,
                pageable
        );
    }

    @Override
    public Optional<SingleWork> findActiveById(Long id) {
        return singleWorkRepository.findActiveById(id);
    }
}
