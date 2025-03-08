package com.benchpress200.photique.singlework.infrastructure;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SingleWorkRepository extends JpaRepository<SingleWork, Long>, SingleWorkRepositoryCustom {
    List<SingleWork> findByWriter(User writer);

    Long countByWriter(User writer);
}
