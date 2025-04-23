package com.benchpress200.photique.exhibition.infrastructure;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.user.domain.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {
    List<Exhibition> findByWriter(User user);

    Long countByWriter(User writer);

    List<Exhibition> findAllByUpdatedAtAfter(LocalDateTime time);
}
