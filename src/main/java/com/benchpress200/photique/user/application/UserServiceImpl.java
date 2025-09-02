package com.benchpress200.photique.user.application;

import com.benchpress200.photique.exhibition.domain.ExhibitionCommentDomainService;
import com.benchpress200.photique.exhibition.domain.ExhibitionDomainService;
import com.benchpress200.photique.image.domain.ImageDomainService;
import com.benchpress200.photique.singlework.domain.SingleWorkCommentDomainService;
import com.benchpress200.photique.singlework.domain.SingleWorkDomainService;
import com.benchpress200.photique.user.application.cache.UserCacheService;
import com.benchpress200.photique.user.domain.FollowDomainService;
import com.benchpress200.photique.user.domain.UserDomainService;
import com.benchpress200.photique.user.domain.dto.NicknameValidationRequest;
import com.benchpress200.photique.user.domain.dto.UserDetailsRequest;
import com.benchpress200.photique.user.domain.dto.UserDetailsResponse;
import com.benchpress200.photique.user.domain.dto.UserSearchRequest;
import com.benchpress200.photique.user.domain.dto.UserSearchResponse;
import com.benchpress200.photique.user.domain.entity.UserSearch;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDomainService userDomainService;
    private final FollowDomainService followDomainService;
    private final ImageDomainService imageDomainService;
    private final SingleWorkDomainService singleWorkDomainService;
    private final SingleWorkCommentDomainService singleWorkCommentDomainService;
    private final ExhibitionDomainService exhibitionDomainService;
    private final ExhibitionCommentDomainService exhibitionCommentDomainService;
    private final UserCacheService userCacheService;

    @Override
    public void validateNickname(final NicknameValidationRequest nicknameValidationRequest) {
        // 닉네임 중복검사 수행
        String nickname = nicknameValidationRequest.getNickname();
        userDomainService.isDuplicatedNickname(nickname);
    }

    @Override
    @Transactional
    public UserDetailsResponse getUserDetails(final UserDetailsRequest userDetailsRequest) {
        // 캐시 데이터 우선 조회 서비스 호출
        UserDetailsResponse userDetailsResponse = userCacheService.getUserDetails(userDetailsRequest);

        // 요청유저의 팔로우 여부 조회
        // 각각의 요청 유저마다 팔로잉 상태는 다르므로 해당 값은 캐싱에서 제외
        long userId = userDetailsRequest.getUserId();
        long requestUserId = userDetailsRequest.getRequestUserId();
        boolean isFollowing = followDomainService.isFollowing(requestUserId, userId);

        // 팔로잉 여부 할당
        userDetailsResponse.updateFollowingStatus(isFollowing);

        // 유저상세조회 dto 반환
        return userDetailsResponse;
    }

    @Override
    @Transactional
    public Page<UserSearchResponse> searchUsers(
            final UserSearchRequest userSearchRequest,
            final Pageable pageable
    ) {
        // 키워드 조회
        String keyword = userSearchRequest.getKeyword();
        Page<UserSearch> userSearchPage = userDomainService.searchUsers(keyword, pageable);

        // 검색 유저중 팔로우 상태 조회
        Long userId = userSearchRequest.getUserId();

        List<UserSearchResponse> userSearchResponseList = userSearchPage.stream()
                .map(userSearch -> {
                    boolean isFollowing = followDomainService.isFollowing(userId, userSearch.getId());
                    return UserSearchResponse.of(userSearch, isFollowing);
                })
                .toList();

        return new PageImpl<>(userSearchResponseList, pageable, userSearchPage.getTotalElements());
    }
}
