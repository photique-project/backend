package com.benchpress200.photique.singlework.infrastructure.persistence.jpa;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkLike;
import com.benchpress200.photique.singlework.domain.entity.id.SingleWorkLikeId;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SingleWorkLikeRepository extends JpaRepository<SingleWorkLike, SingleWorkLikeId> {
    Long countBySingleWork(SingleWork singleWork);

    boolean existsByUserIdAndSingleWorkId(Long userId, Long singleWorkId);

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

    @Query("""
            SELECT sl
            FROM SingleWorkLike sl
            JOIN FETCH sl.user
            JOIN FETCH sl.singleWork
            WHERE sl.user.id = :userId
            AND (
               :keyword IS NULL
               OR sl.singleWork.title LIKE CONCAT('%', :keyword, '%')
            )
            AND sl.singleWork.deletedAt IS NULL
            """)
    Page<SingleWorkLike> searchLikedSingleWorkByDeletedAtIsNull(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
