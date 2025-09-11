package com.benchpress200.photique.user.domain;

import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.repository.UserRepository;
import com.benchpress200.photique.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDomainServiceImpl implements UserDomainService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void isDuplicatedEmail(final String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserException("Email [" + email + "] is already in use.", HttpStatus.CONFLICT);
        }
    }

    @Override
    public void isDuplicatedNickname(final String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new UserException("Nickname [" + nickname + "] is already in use.", HttpStatus.CONFLICT);
        }
    }

    @Override
    public String encodePassword(final String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public User findUser(final Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserException("User with id {" + userId + "} is not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public User findUser(final String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UserException("User with email {" + email + "} is not found", HttpStatus.NOT_FOUND)
        );
    }
}