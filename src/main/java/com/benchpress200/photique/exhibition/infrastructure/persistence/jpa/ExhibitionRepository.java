package com.benchpress200.photique.exhibition.infrastructure.persistence.jpa;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.user.domain.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {
    List<Exhibition> findByWriter(User user);

    Long countByWriter(User writer);

    List<Exhibition> findAllByUpdatedAtAfter(LocalDateTime time);

    @Query("SELECT DISTINCT e FROM Exhibition e JOIN FETCH e.writer JOIN FETCH e.exhibitionWorks WHERE e.id = :id")
    Optional<Exhibition> findWithWorksAndWriter(@Param("id") Long id);
}
