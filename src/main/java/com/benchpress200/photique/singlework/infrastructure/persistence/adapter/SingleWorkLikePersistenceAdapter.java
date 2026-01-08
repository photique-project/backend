package com.benchpress200.photique.singlework.infrastructure.persistence.adapter;

import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkLikeCommandPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkLikeQueryPort;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkLike;
import com.benchpress200.photique.singlework.infrastructure.persistence.jpa.SingleWorkLikeRepository;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public void delete(SingleWorkLike singleWorkLike) {
        singleWorkLikeRepository.delete(singleWorkLike);
    }

    @Override
    public Optional<SingleWorkLike> findByUserAndSingleWork(User user, SingleWork singleWork) {
        return singleWorkLikeRepository.findByUserAndSingleWork(user, singleWork);
    }

    @Override
    public Page<SingleWorkLike> searchLikedSingleWorkByDeletedAtIsNull(
            Long userId,
            String keyword,
            Pageable pageable
    ) {
        return singleWorkLikeRepository.searchLikedSingleWorkByDeletedAtIsNull(userId, keyword, pageable);
    }
}
