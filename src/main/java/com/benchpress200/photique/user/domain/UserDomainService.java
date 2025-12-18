package com.benchpress200.photique.user.domain;

import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.repository.UserRepository;
import com.benchpress200.photique.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDomainService {
    private final UserRepository userRepository;

    public void isDuplicatedEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserException("Email [" + email + "] is already in use.", HttpStatus.CONFLICT);
        }
    }

    public User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserException("User with id {" + userId + "} is not found", HttpStatus.NOT_FOUND));
    }

    public User findUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UserException("User with email {" + email + "} is not found", HttpStatus.NOT_FOUND)
        );
    }
}