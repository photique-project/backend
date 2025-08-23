package com.benchpress200.photique.singlework.domain.repository;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.user.domain.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SingleWorkRepository extends JpaRepository<SingleWork, Long>, SingleWorkRepositoryCustom {
    List<SingleWork> findByWriter(User writer);

    Long countByWriter(User writer);

    List<SingleWork> findAllByUpdatedAtAfter(LocalDateTime time);

    @Query("SELECT sw FROM SingleWork sw JOIN FETCH sw.writer WHERE sw.id = :id")
    Optional<SingleWork> findWithWriter(@Param("id") Long id);
}
