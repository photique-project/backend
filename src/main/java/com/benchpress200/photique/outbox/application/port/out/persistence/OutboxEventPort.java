package com.benchpress200.photique.outbox.application.port.out.persistence;

import com.benchpress200.photique.outbox.domain.entity.OutboxEvent;

public interface OutboxEventPort {
    OutboxEvent save(OutboxEvent outboxEvent);
}
