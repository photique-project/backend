package com.benchpress200.photique.singlework.infrastructure;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SingleWorkRepository extends JpaRepository<SingleWork, Long> {
}
