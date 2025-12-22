package com.benchpress200.photique.singlework.domain.repository;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SingleWorkRepository extends JpaRepository<SingleWork, Long>, SingleWorkRepositoryCustom {
    Long countByWriter(User writer);

    @Query(
            "SELECT sw " +
                    "FROM SingleWork sw " +
                    "JOIN FETCH sw.writer " +
                    "WHERE sw.id = :id"
    )
    Optional<SingleWork> findWithWriter(@Param("id") Long id);
}
