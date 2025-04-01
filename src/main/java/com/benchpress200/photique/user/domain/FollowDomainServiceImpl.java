package com.benchpress200.photique.user.domain;

import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.exception.UserException;
import com.benchpress200.photique.user.infrastructure.FollowRepository;
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
    public void createFollow(final Follow follow) {
        // 이미 팔로우 중인지 확인하는 요청
        Long followerId = follow.getFollower().getId();
        Long followingId = follow.getFollowing().getId();

        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new UserException("Already following the user", HttpStatus.CONFLICT);
        }

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
        Page<Follow> followers = followRepository.findByFollowingId(user.getId(), pageable);

        if (followers.getTotalElements() == 0) {
            throw new UserException("No users found.", HttpStatus.NOT_FOUND);
        }

        return followers;
    }

    @Override
    public List<Follow> getFollowers(final User user) {
        Long userId = user.getId();
        return followRepository.findByFollowingId(userId);
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
    public List<Follow> getFollowings(final User user) {
        return followRepository.findByFollower(user);
    }

    @Override
    public void deleteFollow(final User user) {
        followRepository.deleteByFollowerOrFollowing(user, user);
    }

    @Override
    public Long countFollowers(final User user) {
        return followRepository.countByFollowing(user);
    }

    @Override
    public Long countFollowings(final User user) {
        return followRepository.countByFollower(user);
    }

    @Override
    public boolean isFollowing(
            final Long followerId,
            final Long followingId
    ) {
        if (followerId == 0) {
            return false;
        }

        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }
}
