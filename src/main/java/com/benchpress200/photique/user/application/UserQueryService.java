package com.benchpress200.photique.user.application;

import com.benchpress200.photique.auth.domain.port.AuthenticationUserProviderPort;
import com.benchpress200.photique.auth.domain.result.AuthenticationUserResult;
import com.benchpress200.photique.exhibition.domain.repository.ExhibitionRepository;
import com.benchpress200.photique.singlework.domain.repository.SingleWorkRepository;
import com.benchpress200.photique.user.application.exception.UserNotFoundException;
import com.benchpress200.photique.user.application.query.ValidateNicknameQuery;
import com.benchpress200.photique.user.application.result.MyDetailsResult;
import com.benchpress200.photique.user.application.result.UserDetailsResult;
import com.benchpress200.photique.user.application.result.ValidateNicknameResult;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.repository.FollowRepository;
import com.benchpress200.photique.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryService implements UserDetailsService {
    private final UserRepository userRepository;
    private final SingleWorkRepository singleWorkRepository;
    private final ExhibitionRepository exhibitionRepository;
    private final FollowRepository followRepository;
    private final AuthenticationUserProviderPort authenticationUserProviderPort;

    // @Transactional이 없기 때문에 조회 쿼리가 나갈 때 커넥션을 얻고 MySQL 오토커밋
    // 결과셋이 애플리케이션으로 반환되면 바로 커넥션 반납
    public ValidateNicknameResult validateNickname(final ValidateNicknameQuery validateNicknameQuery) {
        String nickname = validateNicknameQuery.getNickname();
        boolean isDuplicated = userRepository.existsByNickname(nickname);

        return ValidateNicknameResult.of(isDuplicated);
    }

    public UserDetailsResult getUserDetails(final Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 단일작품 카운팅
        Long singleWorkCount = singleWorkRepository.countByWriter(user);

        // 전시회 카운팅
        Long exhibitionCount = exhibitionRepository.countByWriter(user);

        // 본인'을' 팔로우하는 유저 카운팅
        Long followerCount = followRepository.countByFollowing(user);

        // 본인'이' 팔로우하는 유저 카운팅
        Long followingCount = followRepository.countByFollower(user);

        // 요청 유저의 팔로우 유무확인을 위한 조회
        Long currentUserId = authenticationUserProviderPort.getCurrentUserId();

        // 본인 팔로우 유무 조회
        boolean isFollowing = followRepository.existsByFollowerIdAndFollowingId(currentUserId, userId);

        return UserDetailsResult.of(
                user,
                singleWorkCount,
                exhibitionCount,
                followerCount,
                followingCount,
                isFollowing
        );
    }

    public MyDetailsResult getMyDetails() {
        // 인증된 유저 id 조회
        Long userId = authenticationUserProviderPort.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 단일작품 카운팅
        Long singleWorkCount = singleWorkRepository.countByWriter(user);

        // 전시회 카운팅
        Long exhibitionCount = exhibitionRepository.countByWriter(user);

        // 본인'을' 팔로우하는 유저 카운팅
        Long followerCount = followRepository.countByFollowing(user);

        // 본인'이' 팔로우하는 유저 카운팅
        Long followingCount = followRepository.countByFollower(user);

        return MyDetailsResult.of(
                user,
                singleWorkCount,
                exhibitionCount,
                followerCount,
                followingCount
        );
    }


    // 스프링 시큐리티를 위한 오버라이딩 메서드
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return AuthenticationUserResult.from(user);
    }
}
