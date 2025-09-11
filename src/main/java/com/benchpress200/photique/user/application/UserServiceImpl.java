package com.benchpress200.photique.user.application;

import com.benchpress200.photique.user.domain.dto.UserSearchRequest;
import com.benchpress200.photique.user.domain.dto.UserSearchResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {

    @Transactional
    public Page<UserSearchResponse> searchUsers(
            final UserSearchRequest userSearchRequest,
            final Pageable pageable
    ) {
        // 키워드 조회
        String keyword = userSearchRequest.getKeyword();
//        Page<UserSearch> userSearchPage = userDomainService.searchUsers(keyword, pageable);
//
//        // 검색 유저중 팔로우 상태 조회
//        Long userId = userSearchRequest.getUserId();
//
//        List<UserSearchResponse> userSearchResponseList = userSearchPage.stream()
//                .map(userSearch -> {
//                    boolean isFollowing = followDomainService.isFollowing(userId, userSearch.getId());
//                    return UserSearchResponse.of(userSearch, isFollowing);
//                })
//                .toList();

//        return new PageImpl<>(userSearchResponseList, pageable, userSearchPage.getTotalElements());
        return null;
    }
}
