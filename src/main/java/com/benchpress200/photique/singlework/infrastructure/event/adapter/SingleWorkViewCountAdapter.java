package com.benchpress200.photique.singlework.infrastructure.event.adapter;

import com.benchpress200.photique.singlework.application.query.port.out.event.SingleWorkViewCountPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SingleWorkViewCountAdapter implements SingleWorkViewCountPort {
    private static final String SINGLEWORK_VIEW_COUNT_KEY = "singlework:view:";
    private final RedisTemplate<String, Long> redisTemplate;

    @Override
    public void incrementViewCount(Long singleWorkId) {
        redisTemplate.opsForValue().increment(SINGLEWORK_VIEW_COUNT_KEY + singleWorkId);
    }
}
