package com.benchpress200.photique.singlework.application.query.port.out.persistence;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkLike;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SingleWorkLikeQueryPort {
    Long countBySingleWork(SingleWork singleWork);

    boolean existsByUserIdAndSingleWorkId(Long userId, Long singleWorkId);

    Set<Long> findSingleWorkIds(Long userId, List<Long> singleWorkIds);

    Optional<SingleWorkLike> findByUserAndSingleWork(User user, SingleWork singleWork);

    Page<SingleWorkLike> searchLikedSingleWorkByDeletedAtIsNull(
            Long userId,
            String keyword,
            Pageable pageable
    );
}
