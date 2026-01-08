package com.benchpress200.photique.user.application;

import com.benchpress200.photique.TestContainerConfiguration;
import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.notification.infrastructure.persistence.jpa.NotificationRepository;
import com.benchpress200.photique.user.application.command.service.FollowCommandService;
import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.enumeration.Provider;
import com.benchpress200.photique.user.domain.enumeration.Role;
import com.benchpress200.photique.user.domain.exception.DuplicatedFollowException;
import com.benchpress200.photique.user.domain.exception.InvalidFollowRequestException;
import com.benchpress200.photique.user.domain.exception.UserNotFoundException;
import com.benchpress200.photique.user.infrastructure.persistence.jpa.FollowRepository;
import com.benchpress200.photique.user.infrastructure.persistence.jpa.UserRepository;
import com.benchpress200.photique.util.DummyGenerator;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
@DisplayName("FollowCommandService 테스트")
@ActiveProfiles("test")
@Import(TestContainerConfiguration.class)
public class FollowCommandServiceTest {
    @MockitoSpyBean
    AuthenticationUserProviderPort authenticationUserProviderPort;

    @Autowired
    UserRepository userRepository;

    @MockitoSpyBean
    FollowRepository followRepository;

    @MockitoSpyBean
    NotificationRepository notificationRepository;

    @Autowired
    FollowCommandService followCommandService;

    @AfterEach
    void cleanUp() {
        notificationRepository.deleteAll();
        followRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("follow 커밋 테스트")
    void follow_커밋_테스트() {
        // GIVEN
        String followerEmail = DummyGenerator.generateEmail();
        String followerNickname = DummyGenerator.generateNickname();
        String followerPassword = DummyGenerator.generatePassword();
        String followeeEmail = DummyGenerator.generateEmail();
        String followeeNickname = DummyGenerator.generateNickname();
        String followeePassword = DummyGenerator.generatePassword();

        User follower = User.builder()
                .email(followerEmail)
                .nickname(followerNickname)
                .password(followerPassword)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        User followee = User.builder()
                .email(followeeEmail)
                .nickname(followeeNickname)
                .password(followeePassword)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        follower = userRepository.save(follower);
        followee = userRepository.save(followee);

        Long followerId = follower.getId();
        Long followeeId = followee.getId();

        Mockito.doReturn(followerId).when(authenticationUserProviderPort).getCurrentUserId();

        // WHEN
        followCommandService.follow(followeeId);
        Optional<Follow> follow = followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId);
        List<Notification> notifications = notificationRepository.findByReceiver(followee);

        // THEN
        Assertions.assertThat(follow.isPresent()).isTrue();
        Assertions.assertThat(notifications.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("follow 롤백 테스트 - 본인 팔로우")
    void follow_롤백_테스트_본인_팔로우() {
        // GIVEN
        String followerEmail = DummyGenerator.generateEmail();
        String followerNickname = DummyGenerator.generateNickname();
        String followerPassword = DummyGenerator.generatePassword();

        User follower = User.builder()
                .email(followerEmail)
                .nickname(followerNickname)
                .password(followerPassword)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        follower = userRepository.save(follower);
        Long followerId = follower.getId();

        Mockito.doReturn(followerId).when(authenticationUserProviderPort).getCurrentUserId();

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> followCommandService.follow(followerId))
                .isInstanceOf(InvalidFollowRequestException.class);
    }

    @Test
    @DisplayName("follow 롤백 테스트 - 중복 팔로우")
    void follow_롤백_테스트_중복_팔로우() {
        // GIVEN
        String followerEmail = DummyGenerator.generateEmail();
        String followerNickname = DummyGenerator.generateNickname();
        String followerPassword = DummyGenerator.generatePassword();
        String followeeEmail = DummyGenerator.generateEmail();
        String followeeNickname = DummyGenerator.generateNickname();
        String followeePassword = DummyGenerator.generatePassword();

        User follower = User.builder()
                .email(followerEmail)
                .nickname(followerNickname)
                .password(followerPassword)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        User followee = User.builder()
                .email(followeeEmail)
                .nickname(followeeNickname)
                .password(followeePassword)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        follower = userRepository.save(follower);
        followee = userRepository.save(followee);

        Long followerId = follower.getId();
        Long followeeId = followee.getId();

        Mockito.doReturn(followerId).when(authenticationUserProviderPort).getCurrentUserId();

        // WHEN and THEN
        followCommandService.follow(followeeId);
        Assertions.assertThatThrownBy(() -> followCommandService.follow(followeeId))
                .isInstanceOf(DuplicatedFollowException.class);
    }

    @Test
    @DisplayName("follow 롤백 테스트 - 존재하지 않는 팔로워")
    void follow_롤백_테스트_존재하지_않는_팔로워() {
        // GIVEN
        String followerEmail = DummyGenerator.generateEmail();
        String followerNickname = DummyGenerator.generateNickname();
        String followerPassword = DummyGenerator.generatePassword();
        String followeeEmail = DummyGenerator.generateEmail();
        String followeeNickname = DummyGenerator.generateNickname();
        String followeePassword = DummyGenerator.generatePassword();

        User follower = User.builder()
                .email(followerEmail)
                .nickname(followerNickname)
                .password(followerPassword)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        User followee = User.builder()
                .email(followeeEmail)
                .nickname(followeeNickname)
                .password(followeePassword)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        follower = userRepository.save(follower);
        followee = userRepository.save(followee);

        Long followerId = follower.getId();
        Long followeeId = followee.getId();

        Mockito.doReturn(followerId * -1).when(authenticationUserProviderPort).getCurrentUserId();

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> followCommandService.follow(followeeId))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("follow 롤백 테스트 - 존재하지 않는 팔로이")
    void follow_롤백_테스트_존재하지_않는_팔로이() {
        // GIVEN
        String followerEmail = DummyGenerator.generateEmail();
        String followerNickname = DummyGenerator.generateNickname();
        String followerPassword = DummyGenerator.generatePassword();
        String followeeEmail = DummyGenerator.generateEmail();
        String followeeNickname = DummyGenerator.generateNickname();
        String followeePassword = DummyGenerator.generatePassword();

        User follower = User.builder()
                .email(followerEmail)
                .nickname(followerNickname)
                .password(followerPassword)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        User followee = User.builder()
                .email(followeeEmail)
                .nickname(followeeNickname)
                .password(followeePassword)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        follower = userRepository.save(follower);
        followee = userRepository.save(followee);

        Long followerId = follower.getId();
        Long followeeId = followee.getId();

        Mockito.doReturn(followerId).when(authenticationUserProviderPort).getCurrentUserId();

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> followCommandService.follow(followeeId * -1))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("follow 롤백 테스트 - 팔로우 저장 실패")
    void follow_롤백_테스트_팔로우_저장_실패() {
        // GIVEN
        String followerEmail = DummyGenerator.generateEmail();
        String followerNickname = DummyGenerator.generateNickname();
        String followerPassword = DummyGenerator.generatePassword();
        String followeeEmail = DummyGenerator.generateEmail();
        String followeeNickname = DummyGenerator.generateNickname();
        String followeePassword = DummyGenerator.generatePassword();

        User follower = User.builder()
                .email(followerEmail)
                .nickname(followerNickname)
                .password(followerPassword)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        User followee = User.builder()
                .email(followeeEmail)
                .nickname(followeeNickname)
                .password(followeePassword)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        follower = userRepository.save(follower);
        followee = userRepository.save(followee);

        Long followerId = follower.getId();
        Long followeeId = followee.getId();

        Mockito.doReturn(followerId).when(authenticationUserProviderPort).getCurrentUserId();
        Mockito.doThrow(RuntimeException.class).when(followRepository).save(Mockito.any());

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> followCommandService.follow(followeeId))
                .isInstanceOf(RuntimeException.class);

        Optional<Follow> follow = followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId);
        List<Notification> notifications = notificationRepository.findByReceiver(followee);

        Assertions.assertThat(follow.isPresent()).isFalse();
        Assertions.assertThat(notifications.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("follow 롤백 테스트 - 팔로우 알림 저장 실패")
    void follow_롤백_테스트_팔로우_알림_저장_실패() {
        // GIVEN
        String followerEmail = DummyGenerator.generateEmail();
        String followerNickname = DummyGenerator.generateNickname();
        String followerPassword = DummyGenerator.generatePassword();
        String followeeEmail = DummyGenerator.generateEmail();
        String followeeNickname = DummyGenerator.generateNickname();
        String followeePassword = DummyGenerator.generatePassword();

        User follower = User.builder()
                .email(followerEmail)
                .nickname(followerNickname)
                .password(followerPassword)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        User followee = User.builder()
                .email(followeeEmail)
                .nickname(followeeNickname)
                .password(followeePassword)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        follower = userRepository.save(follower);
        followee = userRepository.save(followee);

        Long followerId = follower.getId();
        Long followeeId = followee.getId();

        Mockito.doReturn(followerId).when(authenticationUserProviderPort).getCurrentUserId();
        Mockito.doThrow(RuntimeException.class).when(notificationRepository).save(Mockito.any());

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> followCommandService.follow(followeeId))
                .isInstanceOf(RuntimeException.class);

        Optional<Follow> follow = followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId);
        List<Notification> notifications = notificationRepository.findByReceiver(followee);

        Assertions.assertThat(follow.isPresent()).isFalse();
        Assertions.assertThat(notifications.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("unfollow 커밋 테스트")
    void unfollow_커밋_테스트() {
        // GIVEN
        String followerEmail = DummyGenerator.generateEmail();
        String followerNickname = DummyGenerator.generateNickname();
        String followerPassword = DummyGenerator.generatePassword();
        String followeeEmail = DummyGenerator.generateEmail();
        String followeeNickname = DummyGenerator.generateNickname();
        String followeePassword = DummyGenerator.generatePassword();

        User follower = User.builder()
                .email(followerEmail)
                .nickname(followerNickname)
                .password(followerPassword)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        User followee = User.builder()
                .email(followeeEmail)
                .nickname(followeeNickname)
                .password(followeePassword)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        follower = userRepository.save(follower);
        followee = userRepository.save(followee);

        Long followerId = follower.getId();
        Long followeeId = followee.getId();

        Mockito.doReturn(followerId).when(authenticationUserProviderPort).getCurrentUserId();

        followCommandService.follow(followeeId);

        // WHEN
        followCommandService.unfollow(followeeId);
        Optional<Follow> optionalFollow = followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId);

        // THEN
        Assertions.assertThat(optionalFollow.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("unfollow 롤백 테스트 - 이미 언팔로우")
    void unfollow_롤백_테스트_이미_언팔로우() {
        // GIVEN
        String followerEmail = DummyGenerator.generateEmail();
        String followerNickname = DummyGenerator.generateNickname();
        String followerPassword = DummyGenerator.generatePassword();
        String followeeEmail = DummyGenerator.generateEmail();
        String followeeNickname = DummyGenerator.generateNickname();
        String followeePassword = DummyGenerator.generatePassword();

        User follower = User.builder()
                .email(followerEmail)
                .nickname(followerNickname)
                .password(followerPassword)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        User followee = User.builder()
                .email(followeeEmail)
                .nickname(followeeNickname)
                .password(followeePassword)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        follower = userRepository.save(follower);
        followee = userRepository.save(followee);

        Long followerId = follower.getId();
        Long followeeId = followee.getId();

        Mockito.doReturn(followerId).when(authenticationUserProviderPort).getCurrentUserId();

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> followCommandService.unfollow(followeeId))
                .isInstanceOf(AlreadyUnfollowException.class);

        Optional<Follow> optionalFollow = followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId);
        Assertions.assertThat(optionalFollow.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("unfollow 롤백 테스트 - 팔로워 조회 실패")
    void unfollow_롤백_테스트_팔로워_조회_실패() {
        // GIVEN
        String followerEmail = DummyGenerator.generateEmail();
        String followerNickname = DummyGenerator.generateNickname();
        String followerPassword = DummyGenerator.generatePassword();
        String followeeEmail = DummyGenerator.generateEmail();
        String followeeNickname = DummyGenerator.generateNickname();
        String followeePassword = DummyGenerator.generatePassword();

        User follower = User.builder()
                .email(followerEmail)
                .nickname(followerNickname)
                .password(followerPassword)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        User followee = User.builder()
                .email(followeeEmail)
                .nickname(followeeNickname)
                .password(followeePassword)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        follower = userRepository.save(follower);
        followee = userRepository.save(followee);

        Long followerId = follower.getId();
        Long followeeId = followee.getId();

        Mockito.doReturn(followerId).when(authenticationUserProviderPort).getCurrentUserId();
        followCommandService.follow(followeeId);

        Follow follow = Follow.of(follower, followee);
        Mockito.doReturn(Optional.of(follow)).when(followRepository)
                .findByFollowerIdAndFolloweeId(Mockito.any(), Mockito.any());
        Mockito.doReturn(followerId * -1).when(authenticationUserProviderPort).getCurrentUserId();

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> followCommandService.unfollow(followeeId))
                .isInstanceOf(UserNotFoundException.class);

        Optional<Follow> optionalFollow = followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId);
        Assertions.assertThat(optionalFollow.isPresent()).isTrue();
    }

    @Test
    @DisplayName("unfollow 롤백 테스트 - 팔로이 조회 실패")
    void unfollow_롤백_테스트_팔로이_조회_실패() {
        // GIVEN
        String followerEmail = DummyGenerator.generateEmail();
        String followerNickname = DummyGenerator.generateNickname();
        String followerPassword = DummyGenerator.generatePassword();
        String followeeEmail = DummyGenerator.generateEmail();
        String followeeNickname = DummyGenerator.generateNickname();
        String followeePassword = DummyGenerator.generatePassword();

        User follower = User.builder()
                .email(followerEmail)
                .nickname(followerNickname)
                .password(followerPassword)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        User followee = User.builder()
                .email(followeeEmail)
                .nickname(followeeNickname)
                .password(followeePassword)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        follower = userRepository.save(follower);
        followee = userRepository.save(followee);

        Long followerId = follower.getId();
        Long followeeId = followee.getId();

        Mockito.doReturn(followerId).when(authenticationUserProviderPort).getCurrentUserId();
        followCommandService.follow(followeeId);

        Follow follow = Follow.of(follower, followee);
        Mockito.doReturn(Optional.of(follow)).when(followRepository)
                .findByFollowerIdAndFolloweeId(Mockito.any(), Mockito.any());
        Mockito.doReturn(followerId).when(authenticationUserProviderPort).getCurrentUserId();

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> followCommandService.unfollow(followeeId * -1))
                .isInstanceOf(UserNotFoundException.class);

        Optional<Follow> optionalFollow = followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId);
        Assertions.assertThat(optionalFollow.isPresent()).isTrue();
    }

    @Test
    @DisplayName("unfollow 롤백 테스트 - 팔로우 삭제 실패")
    void unfollow_롤백_테스트_팔로우_삭제_실패() {
        // GIVEN
        String followerEmail = DummyGenerator.generateEmail();
        String followerNickname = DummyGenerator.generateNickname();
        String followerPassword = DummyGenerator.generatePassword();
        String followeeEmail = DummyGenerator.generateEmail();
        String followeeNickname = DummyGenerator.generateNickname();
        String followeePassword = DummyGenerator.generatePassword();

        User follower = User.builder()
                .email(followerEmail)
                .nickname(followerNickname)
                .password(followerPassword)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        User followee = User.builder()
                .email(followeeEmail)
                .nickname(followeeNickname)
                .password(followeePassword)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        follower = userRepository.save(follower);
        followee = userRepository.save(followee);

        Long followerId = follower.getId();
        Long followeeId = followee.getId();

        Mockito.doReturn(followerId).when(authenticationUserProviderPort).getCurrentUserId();
        Mockito.doThrow(RuntimeException.class).when(followRepository).delete(Mockito.any());

        followCommandService.follow(followeeId);

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> followCommandService.unfollow(followeeId))
                .isInstanceOf(RuntimeException.class);

        Optional<Follow> optionalFollow = followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId);
        Assertions.assertThat(optionalFollow.isPresent()).isTrue();
    }
}
