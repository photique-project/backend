package com.benchpress200.photique.user.domain;

import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.infrastructure.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowDomainServiceImpl implements FollowDomainService {

    private final FollowRepository followRepository;

    @Override
    public void createFollow(final Follow follow) {
        followRepository.save(follow);
    }

    @Override
    public void deleteFollow(
            final User follower,
            final User following
    ) {
        followRepository.deleteByFollowerAndFollowing(follower, following);
    }

    @Override
    public Page<Follow> getFollowers(
            final User user,
            final Pageable pageable
    ) {
        return followRepository.findByFollowingId(user.getId(), pageable);
    }

    @Override
    public Page<Follow> getFollowings(
            final User user,
            final Pageable pageable
    ) {
        return followRepository.findByFollowerId(user.getId(), pageable);
    }

    @Override
    public void deleteFollow(final User user) {
        followRepository.deleteByFollowerOrFollowing(user, user);
    }
}
