package com.benchpress200.photique.chat.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "exhibition_session")
public class ExhibitionSession {
    @Id
    private String sessionId;
    private Long userId;
    private Long exhibitionId;

    @TimeToLive
    private long timeToLive;
}
