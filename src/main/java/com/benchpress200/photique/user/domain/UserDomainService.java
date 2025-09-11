package com.benchpress200.photique.user.domain;

import com.benchpress200.photique.user.domain.entity.User;

public interface UserDomainService {
    void isDuplicatedEmail(String email);

    void isDuplicatedNickname(String nickname);

    String encodePassword(String password);
    
    User findUser(Long userId);

    User findUser(String email);
}
