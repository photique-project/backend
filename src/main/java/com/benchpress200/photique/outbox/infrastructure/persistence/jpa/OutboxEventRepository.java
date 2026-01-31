package com.benchpress200.photique.outbox.infrastructure.persistence.jpa;

import com.benchpress200.photique.outbox.domain.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
}
