package com.benchpress200.photique.user.application.query.service;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.auth.domain.result.AuthenticationUserResult;
import com.benchpress200.photique.exhibition.application.query.port.out.ExhibitionQueryPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkQueryPort;
import com.benchpress200.photique.user.application.query.model.NicknameValidateQuery;
import com.benchpress200.photique.user.application.query.model.UserSearchQuery;
import com.benchpress200.photique.user.application.query.port.in.GetMyDetailsUseCase;
import com.benchpress200.photique.user.application.query.port.in.GetUserDetailsUseCase;
import com.benchpress200.photique.user.application.query.port.in.SearchUserUseCase;
import com.benchpress200.photique.user.application.query.port.in.ValidateNicknameUseCase;
import com.benchpress200.photique.user.application.query.port.out.persistence.FollowQueryPort;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.application.query.result.MyDetailsResult;
import com.benchpress200.photique.user.application.query.result.NicknameValidateResult;
import com.benchpress200.photique.user.application.query.result.UserDetailsResult;
import com.benchpress200.photique.user.application.query.result.UserSearchResult;
import com.benchpress200.photique.user.application.query.support.FolloweeIds;
import com.benchpress200.photique.user.application.query.support.SearchedUsers;
import com.benchpress200.photique.user.application.query.support.UserIds;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.exception.UserNotFoundException;
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
public class UserQueryService implements
        UserDetailsService,
        ValidateNicknameUseCase,
        GetUserDetailsUseCase,
        GetMyDetailsUseCase,
        SearchUserUseCase {

    private final AuthenticationUserProviderPort authenticationUserProviderPort;

    private final UserQueryPort userQueryPort;
    private final SingleWorkQueryPort singleWorkQueryPort;
    private final ExhibitionQueryPort exhibitionQueryPort;
    private final FollowQueryPort followQueryPort;


    // @Transactional이 없기 때문에 조회 쿼리가 나갈 때 커넥션을 얻고 MySQL 오토커밋
    // 결과셋이 애플리케이션으로 반환되면 바로 커넥션 반납
    public NicknameValidateResult validateNickname(NicknameValidateQuery query) {
        String nickname = query.getNickname();
        boolean isDuplicated = userQueryPort.existsByNickname(nickname);

        return NicknameValidateResult.of(isDuplicated);
    }

    public UserDetailsResult getUserDetails(Long userId) {
        User user = userQueryPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 단일작품 카운팅
        Long singleWorkCount = singleWorkQueryPort.countByWriter(user);

        // 전시회 카운팅
        Long exhibitionCount = exhibitionQueryPort.countByWriter(user);

        // 본인'을' 팔로우하는 유저 카운팅
        Long followerCount = followQueryPort.countByFollowee(user);

        // 본인'이' 팔로우하는 유저 카운팅
        Long followingCount = followQueryPort.countByFollower(user);

        // 요청 유저의 팔로우 유무확인을 위한 조회
        Long currentUserId = authenticationUserProviderPort.getCurrentUserId();

        // 본인 팔로우 유무 조회
        boolean isFollowing = followQueryPort.existsByFollowerIdAndFolloweeId(currentUserId, userId);

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

        User user = userQueryPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 단일작품 카운팅
        Long singleWorkCount = singleWorkQueryPort.countByWriter(user);

        // 전시회 카운팅
        Long exhibitionCount = exhibitionQueryPort.countByWriter(user);

        // 본인'을' 팔로우하는 유저 카운팅
        Long followerCount = followQueryPort.countByFollowee(user);

        // 본인'이' 팔로우하는 유저 카운팅
        Long followingCount = followQueryPort.countByFollower(user);

        return MyDetailsResult.of(
                user,
                singleWorkCount,
                exhibitionCount,
                followerCount,
                followingCount
        );
    }

    public UserSearchResult searchUser(UserSearchQuery query) {
        String keyword = query.getKeyword();
        Pageable pageable = query.getPageable();

        // 요청한 유저 id 조회
        Long currentUserId = authenticationUserProviderPort.getCurrentUserId();

        // 닉네임 접두사 기반 검색
        Page<User> userPage = userQueryPort.findByNicknameContaining(keyword, pageable);

        // 검색한 유저 페이지에서 유저의 id를 일급 컬렉션으로 변환
        UserIds userIds = UserIds.from(userPage);

        // 검색 결과 유저들 중에서 요청 유저가 팔로우한 유저 셋으로 조회
        Set<Long> followeeIdSet = followQueryPort.findFolloweeIds(currentUserId, userIds.values());
        FolloweeIds followeeIds = FolloweeIds.from(followeeIdSet);

        // 각 유저마다 팔로우 여부를 확인
        SearchedUsers searchedUsers = SearchedUsers.of(userPage, followeeIds);

        return UserSearchResult.of(searchedUsers, userPage);
    }


    // 스프링 시큐리티를 위한 오버라이딩 메서드
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userQueryPort.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return AuthenticationUserResult.from(user);
    }
}
