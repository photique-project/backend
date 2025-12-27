package com.benchpress200.photique.user.application;

import com.benchpress200.photique.auth.domain.port.AuthenticationUserProviderPort;
import com.benchpress200.photique.user.application.query.FolloweeSearchQuery;
import com.benchpress200.photique.user.application.query.FollowerSearchQuery;
import com.benchpress200.photique.user.application.result.FolloweeSearchResult;
import com.benchpress200.photique.user.application.result.FollowerSearchResult;
import com.benchpress200.photique.user.application.vo.FolloweeIds;
import com.benchpress200.photique.user.application.vo.SearchedUsers;
import com.benchpress200.photique.user.application.vo.UserIds;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.repository.FollowRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowQueryService {
    private final FollowRepository followRepository;
    private final AuthenticationUserProviderPort authenticationUserProviderPort;

    public FollowerSearchResult searchFollowers(FollowerSearchQuery followerSearchQuery) {
        Long userId = followerSearchQuery.getUserId();
        String keyword = followerSearchQuery.getKeyword();
        Pageable pageable = followerSearchQuery.getPageable();

        // 요청 유저 id 조회
        Long currentUserId = authenticationUserProviderPort.getCurrentUserId();

        // userId를 팔로우하는 유저 닉네임 기반 검색
        // 키워드가 null 이라면 닉네임 사전 순 전체 검색
        Page<User> followerPage = followRepository.searchFollower(userId, keyword, pageable);

        // 검색한 팔로워 페이지에서 유저의 id를 일급 컬렉션으로 변환
        UserIds userIds = UserIds.from(followerPage);

        // 검색 결과 팔로워들 중에서 요청 유저가 팔로우한 유저 셋으로 조회
        Set<Long> followeeIdSet = followRepository.findFolloweeIds(currentUserId, userIds.values());
        FolloweeIds followeeIds = FolloweeIds.from(followeeIdSet);

        // 각 유저마다 팔로우 여부를 확인
        SearchedUsers searchedUsers = SearchedUsers.of(followerPage, followeeIds);

        return FollowerSearchResult.of(searchedUsers, followerPage);
    }

    public FolloweeSearchResult searchFollowees(FolloweeSearchQuery followeeSearchQuery) {
        Long userId = followeeSearchQuery.getUserId();
        String keyword = followeeSearchQuery.getKeyword();
        Pageable pageable = followeeSearchQuery.getPageable();

        // 요청 유저 id 조회
        Long currentUserId = authenticationUserProviderPort.getCurrentUserId();

        // userId를 팔로우하는 유저 닉네임 기반 검색
        // 키워드가 null 이라면 닉네임 사전 순 전체 검색
        Page<User> followeePage = followRepository.searchFollowee(userId, keyword, pageable);

        // 검색한 팔로이 페이지에서 유저의 id를 일급 컬렉션으로 변환
        UserIds userIds = UserIds.from(followeePage);

        // 검색 결과 팔로워들 중에서 요청 유저가 팔로우한 유저 셋으로 조회
        Set<Long> followeeIdSet = followRepository.findFolloweeIds(currentUserId, userIds.values());
        FolloweeIds followeeIds = FolloweeIds.from(followeeIdSet);

        // 각 유저마다 팔로우 여부를 확인
        SearchedUsers searchedUsers = SearchedUsers.of(followeePage, followeeIds);

        return FolloweeSearchResult.of(searchedUsers, followeePage);
    }
}
