package com.benchpress200.photique.exhibition.infrastructure;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.user.domain.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {
    List<Exhibition> findByWriter(User user);

    Long countByWriter(User writer);

    @Query("SELECT s FROM Exhibition s WHERE s.updatedAt > :since")
    List<Exhibition> findModifiedSince(@Param("since") LocalDateTime since);
}
