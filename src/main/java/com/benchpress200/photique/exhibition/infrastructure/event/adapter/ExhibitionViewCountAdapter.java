package com.benchpress200.photique.exhibition.infrastructure.event.adapter;

import com.benchpress200.photique.exhibition.application.query.port.out.event.ExhibitionViewCountPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExhibitionViewCountAdapter implements ExhibitionViewCountPort {
    private static final String EXHIBITION_VIEW_COUNT_KEY = "exhibition:view:";
    private final RedisTemplate<String, Long> redisTemplate;


    @Override
    public void incrementViewCount(Long exhibitionId) {
        redisTemplate.opsForValue().increment(EXHIBITION_VIEW_COUNT_KEY + exhibitionId);
    }
}
