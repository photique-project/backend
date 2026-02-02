package com.benchpress200.photique.outbox.application.factory;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.outbox.application.exception.OutboxPayloadSerializationException;
import com.benchpress200.photique.outbox.application.payload.ExhibitionPayload;
import com.benchpress200.photique.outbox.application.payload.SingleWorkPayload;
import com.benchpress200.photique.outbox.application.payload.UserPayload;
import com.benchpress200.photique.outbox.domain.entity.OutboxEvent;
import com.benchpress200.photique.outbox.domain.enumeration.AggregateType;
import com.benchpress200.photique.outbox.domain.enumeration.EventType;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.user.domain.entity.User;
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

    public OutboxEvent singleWorkUpdated(
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
                    .eventType(EventType.UPDATE)
                    .payload(payload)
                    .build();
        } catch (JsonProcessingException e) {
            throw new OutboxPayloadSerializationException();
        }
    }

    public OutboxEvent singleWorkDeleted(SingleWork singleWork) {
        String aggregateId = singleWork.getId().toString();

        return OutboxEvent.builder()
                .aggregateType(AggregateType.SINGLEWORK)
                .aggregateId(aggregateId)
                .eventType(EventType.DELETE)
                .build();
    }

    public OutboxEvent exhibitionCreated(
            Exhibition exhibition,
            List<String> tagNames
    ) {
        try {
            String aggregateId = exhibition.getId().toString();
            ExhibitionPayload exhibitionPayload = ExhibitionPayload.of(exhibition, tagNames);
            String payload = objectMapper.writeValueAsString(exhibitionPayload);

            return OutboxEvent.builder()
                    .aggregateType(AggregateType.EXHIBITION)
                    .aggregateId(aggregateId)
                    .eventType(EventType.CREATE)
                    .payload(payload)
                    .build();
        } catch (JsonProcessingException e) {
            throw new OutboxPayloadSerializationException();
        }
    }

    public OutboxEvent exhibitionUpdated(
            Exhibition exhibition,
            List<String> tagNames
    ) {
        try {
            String aggregateId = exhibition.getId().toString();
            ExhibitionPayload exhibitionPayload = ExhibitionPayload.of(exhibition, tagNames);
            String payload = objectMapper.writeValueAsString(exhibitionPayload);

            return OutboxEvent.builder()
                    .aggregateType(AggregateType.EXHIBITION)
                    .aggregateId(aggregateId)
                    .eventType(EventType.UPDATE)
                    .payload(payload)
                    .build();
        } catch (JsonProcessingException e) {
            throw new OutboxPayloadSerializationException();
        }
    }

    public OutboxEvent exhibitionDeleted(Exhibition exhibition) {
        String aggregateId = exhibition.getId().toString();

        return OutboxEvent.builder()
                .aggregateType(AggregateType.EXHIBITION)
                .aggregateId(aggregateId)
                .eventType(EventType.DELETE)
                .build();
    }

    public OutboxEvent userUpdated(User user) {
        try {
            String aggregateId = user.getId().toString();
            UserPayload userPayload = UserPayload.from(user);
            String payload = objectMapper.writeValueAsString(userPayload);

            return OutboxEvent.builder()
                    .aggregateType(AggregateType.USER)
                    .aggregateId(aggregateId)
                    .eventType(EventType.UPDATE)
                    .payload(payload)
                    .build();
        } catch (JsonProcessingException e) {
            throw new OutboxPayloadSerializationException();
        }
    }
}
