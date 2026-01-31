package com.benchpress200.photique.outbox.infrastructure.persistence.adapter;

import com.benchpress200.photique.outbox.application.port.out.persistence.OutboxEventPort;
import com.benchpress200.photique.outbox.domain.entity.OutboxEvent;
import com.benchpress200.photique.outbox.infrastructure.persistence.jpa.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class OutboxEventPersistenceAdapter implements OutboxEventPort {
    private final OutboxEventRepository outboxEventRepository;

    @Override
    public OutboxEvent save(OutboxEvent outboxEvent) {
        return outboxEventRepository.save(outboxEvent);
    }
}
