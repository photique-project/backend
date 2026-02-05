package com.benchpress200.photique.outbox.application.factory;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.outbox.application.payload.ExhibitionPayload;
import com.benchpress200.photique.outbox.application.payload.SingleWorkPayload;
import com.benchpress200.photique.outbox.application.payload.UserPayload;
import com.benchpress200.photique.outbox.domain.entity.OutboxEvent;
import com.benchpress200.photique.outbox.domain.enumeration.AggregateType;
import com.benchpress200.photique.outbox.domain.enumeration.EventType;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.user.domain.entity.User;
import com.fasterxml.jackson.databind.JsonNode;
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
        String aggregateId = singleWork.getId().toString();
        SingleWorkPayload singleWorkPayload = SingleWorkPayload.of(singleWork, tagNames);
        JsonNode payload = objectMapper.valueToTree(singleWorkPayload);

        return OutboxEvent.builder()
                .aggregateType(AggregateType.SINGLEWORK)
                .aggregateId(aggregateId)
                .eventType(EventType.CREATED)
                .payload(payload)
                .build();
    }

    public OutboxEvent singleWorkUpdated(
            SingleWork singleWork,
            List<String> tagNames
    ) {
        String aggregateId = singleWork.getId().toString();
        SingleWorkPayload singleWorkPayload = SingleWorkPayload.of(singleWork, tagNames);
        JsonNode payload = objectMapper.valueToTree(singleWorkPayload);

        return OutboxEvent.builder()
                .aggregateType(AggregateType.SINGLEWORK)
                .aggregateId(aggregateId)
                .eventType(EventType.UPDATED)
                .payload(payload)
                .build();
    }

    public OutboxEvent singleWorkDeleted(SingleWork singleWork) {
        Long singleWorkId = singleWork.getId();
        String aggregateId = singleWorkId.toString();
        SingleWorkPayload singleWorkPayload = SingleWorkPayload.of(singleWorkId);
        JsonNode payload = objectMapper.valueToTree(singleWorkPayload);

        return OutboxEvent.builder()
                .aggregateType(AggregateType.SINGLEWORK)
                .aggregateId(aggregateId)
                .eventType(EventType.DELETED)
                .payload(payload)
                .build();
    }

    public OutboxEvent exhibitionCreated(
            Exhibition exhibition,
            List<String> tagNames
    ) {
        String aggregateId = exhibition.getId().toString();
        ExhibitionPayload exhibitionPayload = ExhibitionPayload.of(exhibition, tagNames);
        JsonNode payload = objectMapper.valueToTree(exhibitionPayload);

        return OutboxEvent.builder()
                .aggregateType(AggregateType.EXHIBITION)
                .aggregateId(aggregateId)
                .eventType(EventType.CREATED)
                .payload(payload)
                .build();
    }

    public OutboxEvent exhibitionUpdated(
            Exhibition exhibition,
            List<String> tagNames
    ) {
        String aggregateId = exhibition.getId().toString();
        ExhibitionPayload exhibitionPayload = ExhibitionPayload.of(exhibition, tagNames);
        JsonNode payload = objectMapper.valueToTree(exhibitionPayload);

        return OutboxEvent.builder()
                .aggregateType(AggregateType.EXHIBITION)
                .aggregateId(aggregateId)
                .eventType(EventType.UPDATED)
                .payload(payload)
                .build();
    }

    public OutboxEvent exhibitionDeleted(Exhibition exhibition) {
        Long exhibitionId = exhibition.getId();
        String aggregateId = exhibitionId.toString();
        ExhibitionPayload exhibitionPayload = ExhibitionPayload.of(exhibitionId);
        JsonNode payload = objectMapper.valueToTree(exhibitionPayload);

        return OutboxEvent.builder()
                .aggregateType(AggregateType.EXHIBITION)
                .aggregateId(aggregateId)
                .eventType(EventType.DELETED)
                .payload(payload)
                .build();
    }

    public OutboxEvent userUpdated(User user) {
        String aggregateId = user.getId().toString();
        UserPayload userPayload = UserPayload.from(user);
        JsonNode payload = objectMapper.valueToTree(userPayload);

        return OutboxEvent.builder()
                .aggregateType(AggregateType.USER)
                .aggregateId(aggregateId)
                .eventType(EventType.UPDATED)
                .payload(payload)
                .build();
    }
}
