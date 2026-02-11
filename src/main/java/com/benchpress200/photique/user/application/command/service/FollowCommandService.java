package com.benchpress200.photique.user.application.command.service;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.outbox.application.factory.OutboxEventFactory;
import com.benchpress200.photique.outbox.application.port.out.persistence.OutboxEventPort;
import com.benchpress200.photique.outbox.domain.entity.OutboxEvent;
import com.benchpress200.photique.user.application.command.port.in.FollowUseCase;
import com.benchpress200.photique.user.application.command.port.in.UnfollowUseCase;
import com.benchpress200.photique.user.application.command.port.out.persistence.FollowCommandPort;
import com.benchpress200.photique.user.application.query.port.out.persistence.FollowQueryPort;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.exception.DuplicatedFollowException;
import com.benchpress200.photique.user.domain.exception.InvalidFollowRequestException;
import com.benchpress200.photique.user.domain.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowCommandService implements
        FollowUseCase,
        UnfollowUseCase {
    private final AuthenticationUserProviderPort authenticationUserProviderPort;

    private final UserQueryPort userQueryPort;

    private final FollowCommandPort followCommandPort;
    private final FollowQueryPort followQueryPort;

    private final OutboxEventFactory outboxEventFactory;
    private final OutboxEventPort outboxEventPort;


    public void follow(Long followeeId) {
        // 팔로워 유저 조회
        Long followerId = authenticationUserProviderPort.getCurrentUserId();

        // 본인을 팔로우 한다면
        if (followerId.equals(followeeId)) {
            throw new InvalidFollowRequestException();
        }

        // 이미 팔로우 관계 데이터가 있다면 바로 성공 응답줘서 멱등 처리
        followQueryPort.findByFollowerIdAndFolloweeId(followerId, followeeId)
                .ifPresent(follow -> {
                    throw new DuplicatedFollowException();
                });

        // 팔로워 유저 조회
        User follower = userQueryPort.findByIdAndDeletedAtIsNull(followerId)
                .orElseThrow(() -> new UserNotFoundException(followerId));

        // 팔로잉(팔로우 대상) 유저 조회
        User followee = userQueryPort.findByIdAndDeletedAtIsNull(followeeId)
                .orElseThrow(() -> new UserNotFoundException(followerId));

        // 팔로우 엔티티 저장
        Follow follow = Follow.of(follower, followee);
        followCommandPort.save(follow);

        OutboxEvent outboxEvent = outboxEventFactory.follow(follow);
        outboxEventPort.save(outboxEvent);
    }


    public void unfollow(Long followeeId) {
        // 팔로워 유저 조회
        Long followerId = authenticationUserProviderPort.getCurrentUserId();

        // 본인을 언팔로우 한다면
        if (followerId.equals(followeeId)) {
            throw new InvalidFollowRequestException();
        }

        // 이미 팔로우 관계 데이터가 없다면 바로 성공 응답줘서 멱등 처리
        followQueryPort.findByFollowerIdAndFolloweeId(followerId, followeeId)
                .ifPresent(followCommandPort::delete);
    }
}
