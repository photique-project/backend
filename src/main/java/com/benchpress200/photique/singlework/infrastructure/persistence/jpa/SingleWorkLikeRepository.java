package com.benchpress200.photique.singlework.infrastructure.persistence.jpa;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkLike;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SingleWorkLikeRepository extends JpaRepository<SingleWorkLike, Long> {
    void deleteByUser(User user);

    void deleteBySingleWork(SingleWork singleWork);

    Long countBySingleWork(SingleWork singleWork);

    void deleteByUserAndSingleWork(User user, SingleWork singleWork);

    boolean existsByUserAndSingleWork(User user, SingleWork singleWork);

    boolean existsByUserIdAndSingleWorkId(Long userId, Long singleWorkId);

    List<SingleWorkLike> findByUserId(Long userId);

    Page<SingleWorkLike> findByUserId(Long userId, Pageable pageable);

    @Query("""
            SELECT l.singleWork.id
            FROM SingleWorkLike l
            WHERE l.user.id = :userId
            AND l.singleWork.id IN :singleWorkIds
            """)
    Set<Long> findSingleWorkIds(
            @Param("userId") Long userId,
            @Param("singleWorkIds") List<Long> singleWorkIds
    );

    Optional<SingleWorkLike> findByUserAndSingleWork(User user, SingleWork singleWork);
}
