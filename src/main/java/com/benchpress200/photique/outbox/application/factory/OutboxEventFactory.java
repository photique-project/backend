package com.benchpress200.photique.outbox.application.factory;

import com.benchpress200.photique.outbox.application.exception.OutboxPayloadSerializationException;
import com.benchpress200.photique.outbox.application.payload.SingleWorkPayload;
import com.benchpress200.photique.outbox.domain.entity.OutboxEvent;
import com.benchpress200.photique.outbox.domain.enumeration.AggregateType;
import com.benchpress200.photique.outbox.domain.enumeration.EventType;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventFactory {
    private final ObjectMapper objectMapper;

    public OutboxEvent singleWorkCreated(
            SingleWork singleWork,
            List<String> tagNames
    ) {
        try {
            String aggregateId = singleWork.getId().toString();
            SingleWorkPayload singleWorkPayload = SingleWorkPayload.of(singleWork, tagNames);
            String payload = objectMapper.writeValueAsString(singleWorkPayload);

            return OutboxEvent.builder()
                    .aggregateType(AggregateType.SINGLEWORK)
                    .aggregateId(aggregateId)
                    .eventType(EventType.CREATE)
                    .payload(payload)
                    .build();
        } catch (JsonProcessingException e) {
            throw new OutboxPayloadSerializationException();
        }
    }
}
