package com.benchpress200.photique.outbox.application.factory;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import com.benchpress200.photique.outbox.application.payload.ExhibitionCommentPayload;
import com.benchpress200.photique.outbox.application.payload.ExhibitionPayload;
import com.benchpress200.photique.outbox.application.payload.FollowPayload;
import com.benchpress200.photique.outbox.application.payload.SingleWorkCommentPayload;
import com.benchpress200.photique.outbox.application.payload.SingleWorkPayload;
import com.benchpress200.photique.outbox.application.payload.UserPayload;
import com.benchpress200.photique.outbox.domain.entity.OutboxEvent;
import com.benchpress200.photique.outbox.domain.enumeration.AggregateType;
import com.benchpress200.photique.outbox.domain.enumeration.EventType;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
import com.benchpress200.photique.user.domain.entity.Follow;
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

    public OutboxEvent singleWorkLiked(
            SingleWork singleWork
    ) {
        String aggregateId = singleWork.getId().toString();
        SingleWorkPayload singleWorkPayload = SingleWorkPayload.of(singleWork);
        JsonNode payload = objectMapper.valueToTree(singleWorkPayload);

        return OutboxEvent.builder()
                .aggregateType(AggregateType.SINGLEWORK)
                .aggregateId(aggregateId)
                .eventType(EventType.LIKED)
                .payload(payload)
                .build();
    }

    public OutboxEvent singleWorkUnliked(
            SingleWork singleWork
    ) {
        String aggregateId = singleWork.getId().toString();
        SingleWorkPayload singleWorkPayload = SingleWorkPayload.of(singleWork);
        JsonNode payload = objectMapper.valueToTree(singleWorkPayload);

        return OutboxEvent.builder()
                .aggregateType(AggregateType.SINGLEWORK)
                .aggregateId(aggregateId)
                .eventType(EventType.UNLIKED)
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

    public OutboxEvent singleWorkCommentCreated(SingleWorkComment singleWorkComment) {
        Long commentId = singleWorkComment.getId();
        String aggregateId = commentId.toString();

        SingleWorkCommentPayload singleWorkCommentPayload = SingleWorkCommentPayload.from(singleWorkComment);
        JsonNode payload = objectMapper.valueToTree(singleWorkCommentPayload);

        return OutboxEvent.builder()
                .aggregateType(AggregateType.SINGLEWORK_COMMENT)
                .aggregateId(aggregateId)
                .eventType(EventType.CREATED)
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

    public OutboxEvent exhibitionLiked(
            Exhibition exhibition
    ) {
        String aggregateId = exhibition.getId().toString();
        ExhibitionPayload exhibitionPayload = ExhibitionPayload.of(exhibition);
        JsonNode payload = objectMapper.valueToTree(exhibitionPayload);

        return OutboxEvent.builder()
                .aggregateType(AggregateType.EXHIBITION)
                .aggregateId(aggregateId)
                .eventType(EventType.LIKED)
                .payload(payload)
                .build();
    }

    public OutboxEvent exhibitionUnliked(
            Exhibition exhibition
    ) {
        String aggregateId = exhibition.getId().toString();
        ExhibitionPayload exhibitionPayload = ExhibitionPayload.of(exhibition);
        JsonNode payload = objectMapper.valueToTree(exhibitionPayload);

        return OutboxEvent.builder()
                .aggregateType(AggregateType.EXHIBITION)
                .aggregateId(aggregateId)
                .eventType(EventType.UNLIKED)
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

    public OutboxEvent exhibitionCommentCreated(ExhibitionComment exhibitionComment) {
        Long commentId = exhibitionComment.getId();
        String aggregateId = commentId.toString();
        ExhibitionCommentPayload exhibitionCommentPayload = ExhibitionCommentPayload.from(exhibitionComment);
        JsonNode payload = objectMapper.valueToTree(exhibitionCommentPayload);

        return OutboxEvent.builder()
                .aggregateType(AggregateType.EXHIBITION_COMMENT)
                .aggregateId(aggregateId)
                .eventType(EventType.CREATED)
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

    public OutboxEvent follow(Follow follow) {
        // 팔로워만 팔로우 관계를 맺고 끊을 수 있으므로 key로 팔로워 id 사용
        String aggregateId = follow.getFollower().getId().toString();
        FollowPayload followPayload = FollowPayload.from(follow);
        JsonNode payload = objectMapper.valueToTree(followPayload);

        return OutboxEvent.builder()
                .aggregateType(AggregateType.FOLLOW)
                .aggregateId(aggregateId)
                .eventType(EventType.CREATED)
                .payload(payload)
                .build();
    }
}
