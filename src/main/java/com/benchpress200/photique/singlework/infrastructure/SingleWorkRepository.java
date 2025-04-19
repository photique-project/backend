package com.benchpress200.photique.singlework.infrastructure;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.user.domain.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SingleWorkRepository extends JpaRepository<SingleWork, Long>, SingleWorkRepositoryCustom {
    List<SingleWork> findByWriter(User writer);

    Long countByWriter(User writer);

    @Query("SELECT s FROM SingleWork s WHERE s.updatedAt > :since")
    List<SingleWork> findModifiedSince(@Param("since") LocalDateTime since);
}
