package com.benchpress200.photique.user.infrastructure.persistence.adapter;

import com.benchpress200.photique.user.application.command.port.out.persistence.FollowCommandPort;
import com.benchpress200.photique.user.application.query.port.out.persistence.FollowQueryPort;
import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.infrastructure.persistence.jpa.FollowRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FollowPersistenceAdapter implements
        FollowCommandPort,
        FollowQueryPort {
    private final FollowRepository followRepository;

    @Override
    public Follow save(Follow follow) {
        return followRepository.save(follow);
    }

    @Override
    public void delete(Follow follow) {
        followRepository.delete(follow);
    }

    @Override
    public void deleteByFollowerAndFollowee(User follower, User followee) {
        followRepository.deleteByFollowerAndFollowee(follower, followee);
    }

    @Override
    public Page<Follow> findByFolloweeId(Long followeeId, Pageable pageable) {
        return followRepository.findByFolloweeId(followeeId, pageable);
    }

    @Override
    public List<Follow> findByFolloweeId(Long followeeId) {
        return followRepository.findByFolloweeId(followeeId);
    }

    @Override
    public Page<Follow> findByFollowerId(Long followerId, Pageable pageable) {
        return followRepository.findByFollowerId(followerId, pageable);
    }

    @Override
    public Long countByFollowee(User followee) {
        return followRepository.countByFollowee(followee);
    }

    @Override
    public Long countByFollower(User follower) {
        return followRepository.countByFollower(follower);
    }

    @Override
    public Optional<Follow> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId) {
        return followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    @Override
    public boolean existsByFollowerIdAndFolloweeId(Long followerId, Long followeeId) {
        return followRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    @Override
    public Slice<Follow> findByFolloweeWithFollower(User followee, Pageable pageable) {
        return followRepository.findByFolloweeWithFollower(followee, pageable);
    }

    @Override
    public Set<Long> findFolloweeIds(Long followerId, List<Long> followeeIds) {
        return followRepository.findFolloweeIds(followerId, followeeIds);
    }

    @Override
    public Page<User> searchFollower(Long userId, String keyword, Pageable pageable) {
        return followRepository.searchFollower(userId, keyword, pageable);
    }

    @Override
    public Page<User> searchFollowee(Long userId, String keyword, Pageable pageable) {
        return followRepository.searchFollowee(userId, keyword, pageable);
    }
}
