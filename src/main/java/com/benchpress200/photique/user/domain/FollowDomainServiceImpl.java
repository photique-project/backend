package com.benchpress200.photique.user.domain;

import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.repository.FollowRepository;
import com.benchpress200.photique.user.exception.UserException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowDomainServiceImpl implements FollowDomainService {

    private final FollowRepository followRepository;

    @Override
    public void deleteFollow(
            final User follower,
            final User following
    ) {
        followRepository.deleteByFollowerAndFollowee(follower, following);
    }

    @Override
    public Page<Follow> getFollowers(
            final User user,
            final Pageable pageable
    ) {
        Page<Follow> followers = followRepository.findByFolloweeId(user.getId(), pageable);

        if (followers.getTotalElements() == 0) {
            throw new UserException("No users found.", HttpStatus.NOT_FOUND);
        }

        return followers;
    }

    @Override
    public List<Follow> getFollowers(final User user) {
        Long userId = user.getId();
        return followRepository.findByFolloweeId(userId);
    }

    @Override
    public Page<Follow> getFollowings(
            final User user,
            final Pageable pageable
    ) {
        Page<Follow> followings = followRepository.findByFollowerId(user.getId(), pageable);

        if (followings.getTotalElements() == 0) {
            throw new UserException("No users found.", HttpStatus.NOT_FOUND);
        }

        return followings;
    }


    @Override
    public boolean isFollowing(
            final Long followerId,
            final Long followingId
    ) {
        if (followerId == 0) {
            return false;
        }

        return followRepository.existsByFollowerIdAndFolloweeId(followerId, followingId);
    }
}
