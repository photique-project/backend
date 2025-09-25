package com.benchpress200.photique.user.application;

import com.benchpress200.photique.auth.domain.port.AuthenticationUserProviderPort;
import com.benchpress200.photique.auth.domain.result.AuthenticationUserResult;
import com.benchpress200.photique.exhibition.domain.repository.ExhibitionRepository;
import com.benchpress200.photique.singlework.domain.repository.SingleWorkRepository;
import com.benchpress200.photique.user.application.exception.UserNotFoundException;
import com.benchpress200.photique.user.application.query.SearchUsersQuery;
import com.benchpress200.photique.user.application.query.ValidateNicknameQuery;
import com.benchpress200.photique.user.application.result.MyDetailsResult;
import com.benchpress200.photique.user.application.result.SearchUsersResult;
import com.benchpress200.photique.user.application.result.SearchedUser;
import com.benchpress200.photique.user.application.result.UserDetailsResult;
import com.benchpress200.photique.user.application.result.ValidateNicknameResult;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.repository.FollowRepository;
import com.benchpress200.photique.user.domain.repository.UserRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        Long followerCount = followRepository.countByFollowee(user);

        // 본인'이' 팔로우하는 유저 카운팅
        Long followingCount = followRepository.countByFollower(user);

        // 요청 유저의 팔로우 유무확인을 위한 조회
        Long currentUserId = authenticationUserProviderPort.getCurrentUserId();

        // 본인 팔로우 유무 조회
        boolean isFollowing = followRepository.existsByFollowerIdAndFolloweeId(currentUserId, userId);

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
        Long followerCount = followRepository.countByFollowee(user);

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

    public SearchUsersResult searchUsers(final SearchUsersQuery searchUsersQuery) {
        String keyword = searchUsersQuery.getKeyword();
        Pageable pageable = searchUsersQuery.getPageable();

        // 닉네임 접두사 기반 검색
        Page<User> userPage = userRepository.findByNicknameContaining(keyword, pageable);

        // 요청 유저 id 조회
        Long currentUserId = authenticationUserProviderPort.getCurrentUserId();

        // 검색한 유저 페이지에서 id를 set으로 추출
        List<Long> userIds = userPage.stream()
                .map(User::getId)
                .toList();

        // 검색 결과 유저들 중에서 요청 유저가 팔로우한 유저 셋으로 조회
        Set<Long> followingIds = followRepository.findFolloweeIds(currentUserId, userIds);

        // 각 유저마다 팔로우 여부를 확인
        List<SearchedUser> users = userPage.stream()
                .map(user -> {
                    Long userId = user.getId();
                    boolean isFollowing = followingIds.contains(userId);

                    return SearchedUser.of(user, isFollowing);
                })
                .toList();

        return SearchUsersResult.of(users, userPage);
    }


    // 스프링 시큐리티를 위한 오버라이딩 메서드
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return AuthenticationUserResult.from(user);
    }
}
