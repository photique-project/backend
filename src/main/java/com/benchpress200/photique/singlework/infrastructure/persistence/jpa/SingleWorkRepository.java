package com.benchpress200.photique.singlework.infrastructure.persistence.jpa;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.user.domain.entity.User;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SingleWorkRepository extends JpaRepository<SingleWork, Long> {
    Long countByWriter(User writer);

    @Query("""
            SELECT sw
            FROM SingleWork sw
            WHERE sw.id = :id
            AND sw.deletedAt IS NULL
            """)
    Optional<SingleWork> findActiveById(@Param("id") Long id);

    @Query("""
            SELECT sw
            FROM SingleWork sw
            JOIN FETCH sw.writer
            WHERE sw.id = :id
            """)
    Optional<SingleWork> findByIdWithWriter(@Param("id") Long id);

    @Query("""
            SELECT sw
            FROM SingleWork sw
            JOIN FETCH sw.writer
            WHERE sw.id = :id
            AND sw.deletedAt IS NULL
            """)
    Optional<SingleWork> findActiveByIdWithWriter(@Param("id") Long id);

    // Spring Data JPA는 모든 @Query를 기본적으로 SELECT로 간주
    // 이 상태에서 @Modifying이 없으면  결과를 엔티티/DTO로 매핑하려고 시도하고 런타임 예외 발생
    // @Modifying은 Spring Data JPA에게 쓰기 쿼리임을 알려주는 표시
    @Transactional
    @Modifying
    @Query("""
            UPDATE SingleWork s
            SET s.viewCount = s.viewCount + 1
            WHERE s.id = :singleWorkId
            """)
    void incrementViewCount(@Param("singleWorkId") Long singleWorkId);

    @Modifying
    @Query("""
            UPDATE SingleWork s
            SET s.likeCount = s.likeCount + 1
            WHERE s.id = :singleWorkId
            """)
    void incrementLikeCount(@Param("singleWorkId") Long singleWorkId);

    @Modifying
    @Query("""
            UPDATE SingleWork s
            SET s.likeCount = s.likeCount - 1
            WHERE s.id = :singleWorkId
            """)
    void decrementLikeCount(@Param("singleWorkId") Long singleWorkId);
}
