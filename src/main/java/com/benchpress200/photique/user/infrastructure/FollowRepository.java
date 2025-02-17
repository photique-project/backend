package com.benchpress200.photique.user.infrastructure;

import com.benchpress200.photique.user.domain.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
}
