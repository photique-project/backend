package com.benchpress200.photique.singlework.infrastructure;

import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SingleWorkTagRepository extends JpaRepository<SingleWorkTag, Long> {
    List<SingleWorkTag> findBySingleWorkId(Long singleWorkId);
}
