package com.benchpress200.photique.user.domain;

import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.entity.UserSearch;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserDomainService {
    void isDuplicatedEmail(String email);

    void isDuplicatedNickname(String nickname);

    String encodePassword(String password);
    

    User findUser(Long userId);

    User findUser(String email);

    void updatePassword(User user, String newPassword);

    void updateNickname(User user, String newNickname);

    void updateIntroduction(User user, String newIntroduction);

    void updateProfileImage(User user, String newProfileImage);

    Page<UserSearch> searchUsers(String keyword, Pageable pageable);

    void deleteUser(User user);

    List<User> findUsersModifiedSince(LocalDateTime time);

    void markAsUpdated(User user);

    void updateUserSearch(UserSearch userSearch);

    void updateAllUserSearch(List<UserSearch> userSearches);

}
