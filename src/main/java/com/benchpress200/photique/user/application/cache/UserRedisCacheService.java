package com.benchpress200.photique.user.application.cache;

import com.benchpress200.photique.exhibition.domain.ExhibitionDomainService;
import com.benchpress200.photique.singlework.domain.SingleWorkDomainService;
import com.benchpress200.photique.user.domain.FollowDomainService;
import com.benchpress200.photique.user.domain.UserDomainService;
import com.benchpress200.photique.user.domain.dto.UserDetailsRequest;
import com.benchpress200.photique.user.domain.dto.UserDetailsResponse;
import com.benchpress200.photique.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRedisCacheService implements UserCacheService {
    private final UserDomainService userDomainService;
    private final SingleWorkDomainService singleWorkDomainService;
    private final ExhibitionDomainService exhibitionDomainService;
    private final FollowDomainService followDomainService;

    @Override
    @Cacheable(
            value = "userDetails",
            key = "#userDetailsRequest.userId" // 메서드 파라미터에서 userId 를 캐시 키로 사용
    )
    public UserDetailsResponse getUserDetails(final UserDetailsRequest userDetailsRequest) {
        // 유저 데이터 조회
        Long userId = userDetailsRequest.getUserId();
        User foundUser = userDomainService.findUser(userId);

        // 유저 단일작품 개수 조회
        Long singleWorkCount = singleWorkDomainService.countSingleWork(foundUser);

        // 유저 전시회 개수 조회
        Long exhibitionCount = exhibitionDomainService.countExhibition(foundUser);

        // 유저 팔로워 수 조회
        Long followerCount = followDomainService.countFollowers(foundUser);

        // 유저 팔로잉 수 조회
        Long followingCount = followDomainService.countFollowings(foundUser);

        return UserDetailsResponse.of(
                foundUser,
                singleWorkCount,
                exhibitionCount,
                followerCount,
                followingCount
        );
    }
}
