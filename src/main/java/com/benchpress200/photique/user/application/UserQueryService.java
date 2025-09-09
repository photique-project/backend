package com.benchpress200.photique.user.application;

import com.benchpress200.photique.auth.filter.result.UserAuthenticationResult;
import com.benchpress200.photique.user.application.query.ValidateNicknameQuery;
import com.benchpress200.photique.user.application.result.ValidateNicknameResult;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryService implements UserDetailsService {
    private final UserRepository userRepository;

    // @Transactional이 없기 때문에 조회 쿼리가 나갈 때 커넥션을 얻고 MySQL 오토커밋
    // 결과셋이 애플리케이션으로 반환되면 바로 커넥션 반납
    public ValidateNicknameResult validateNickname(final ValidateNicknameQuery validateNicknameQuery) {
        String nickname = validateNicknameQuery.getNickname();
        boolean isDuplicated = userRepository.existsByNickname(nickname);

        return ValidateNicknameResult.of(isDuplicated);
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return UserAuthenticationResult.from(user);
    }
}
