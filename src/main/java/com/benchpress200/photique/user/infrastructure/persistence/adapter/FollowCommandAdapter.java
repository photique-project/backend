package com.benchpress200.photique.user.infrastructure.persistence.adapter;

import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.port.persistence.FollowCommandPort;
import com.benchpress200.photique.user.infrastructure.persistence.jpa.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FollowCommandAdapter implements FollowCommandPort {
    private final FollowRepository followRepository;

    @Override
    public Follow save(Follow follow) {
        return followRepository.save(follow);
    }

    @Override
    public void delete(Follow follow) {
        followRepository.delete(follow);
    }
}
