package com.benchpress200.photique.singlework.infrastructure.persistence.adapter;

import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkLikeCommandPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkLikeQueryPort;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkLike;
import com.benchpress200.photique.singlework.infrastructure.persistence.jpa.SingleWorkLikeRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SingleWorkLikePersistenceAdapter implements
        SingleWorkLikeQueryPort,
        SingleWorkLikeCommandPort {
    private final SingleWorkLikeRepository singleWorkLikeRepository;

    @Override
    public Long countBySingleWork(SingleWork singleWork) {
        return singleWorkLikeRepository.countBySingleWork(singleWork);
    }

    @Override
    public boolean existsByUserIdAndSingleWorkId(Long userId, Long singleWorkId) {
        return singleWorkLikeRepository.existsByUserIdAndSingleWorkId(userId, singleWorkId);
    }

    @Override
    public Set<Long> findSingleWorkIds(Long userId, List<Long> singleWorkIds) {
        return singleWorkLikeRepository.findSingleWorkIds(userId, singleWorkIds);
    }

    @Override
    public SingleWorkLike save(SingleWorkLike singleWorkLike) {
        return singleWorkLikeRepository.save(singleWorkLike);
    }
}
