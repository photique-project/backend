package com.benchpress200.photique.outbox.domain.support;


import com.benchpress200.photique.outbox.domain.entity.OutboxEvent;
import com.benchpress200.photique.outbox.domain.enumeration.AggregateType;
import com.benchpress200.photique.outbox.domain.enumeration.EventType;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;

public class OutboxEventFixture {
    private OutboxEventFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id = 1L;
        private AggregateType aggregateType;
        private String aggregateId;
        private EventType eventType;
        private JsonNode payload;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder aggregateType(AggregateType aggregateType) {
            this.aggregateType = aggregateType;
            return this;
        }

        public Builder aggregateId(String aggregateId) {
            this.aggregateId = aggregateId;
            return this;
        }

        public Builder eventType(EventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder payload(JsonNode payload) {
            this.payload = payload;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public OutboxEvent build() {
            return OutboxEvent.builder()
                    .id(id)
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .eventType(eventType)
                    .payload(payload)
                    .createdAt(createdAt)
                    .build();
        }
    }
}
