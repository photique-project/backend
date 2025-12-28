package com.benchpress200.photique.user.infrastructure.persistence.adapter;

import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.port.persistence.UserCommandPort;
import com.benchpress200.photique.user.infrastructure.persistence.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserCommandAdapter implements UserCommandPort {
    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}
