package com.benchpress200.photique.user.infrastructure.persistence.adapter;

import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.port.persistence.UserQueryPort;
import com.benchpress200.photique.user.infrastructure.persistence.jpa.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserQueryAdapter implements UserQueryPort {
    private final UserRepository userRepository;


    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Override
    public Page<User> findByNicknameContaining(String keyword, Pageable pageable) {
        return userRepository.findByNicknameContaining(keyword, pageable);
    }

    @Override
    public Page<User> findByNicknameStartingWith(String keyword, Pageable pageable) {
        return userRepository.findByNicknameStartingWith(keyword, pageable);
    }

    @Override
    public Page<User> searchByNicknameFts(String keyword, Pageable pageable) {
        return userRepository.searchByNicknameFts(keyword, pageable);
    }
}
