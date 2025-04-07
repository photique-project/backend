package com.benchpress200.photique.user.domain;

import com.benchpress200.photique.common.transaction.rollbackcontext.ElasticsearchUserRollbackContext;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.entity.UserSearch;
import com.benchpress200.photique.user.exception.UserException;
import com.benchpress200.photique.user.infrastructure.UserRepository;
import com.benchpress200.photique.user.infrastructure.UserSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDomainServiceImpl implements UserDomainService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserSearchRepository userSearchRepository;

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
    public void registerUser(final User user) {
        // 이메일 중복검사
        String email = user.getEmail();
        isDuplicatedEmail(email);

        // 닉네임 중복검사
        String nickname = user.getNickname();
        isDuplicatedNickname(nickname);

        // RDBMS 저장
        User saved = userRepository.save(user);

        // Elasticsearch 엔티티 생성
        UserSearch userSearch = UserSearch.builder()
                .id(saved.getId())
                .profileImage(saved.getProfileImage())
                .nickname(saved.getNickname())
                .introduction(saved.getIntroduction())
                .createdAt(saved.getCreatedAt())
                .build();

        // Elasticsearch 저장 (예외발생 시 롤백을 위한 컨텍스트에 저장)
        ElasticsearchUserRollbackContext.addDocumentToSave(userSearch);
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

    @Override
    public void updatePassword(
            final User user,
            final String newPassword
    ) {
        // 비번이 null 아니면 수정, null 이면 유지
        if (newPassword != null) {
            String encodedPassword = encodePassword(newPassword);
            user.updatePassword(encodedPassword);
        }
    }

    @Override
    public void updateNickname(
            final User user,
            final String newNickname
    ) {
        if (newNickname == null) {
            return;
        }

        isDuplicatedNickname(newNickname);
        user.updateNickname(newNickname);

        Long userId = user.getId();
        UserSearch userSearch = findUserSearch(userId);

        // 엘라스틱서치 데이터 업데이트
        userSearch.updateNickname(newNickname);

        // 업데이트
        ElasticsearchUserRollbackContext.addDocumentToUpdate(userSearch);
    }

    private UserSearch findUserSearch(final Long userId) {
        if (ElasticsearchUserRollbackContext.hasDocumentToUpdate()) {
            return ElasticsearchUserRollbackContext.getDocumentToUpdate();
        }

        return userSearchRepository.findById(userId).orElseThrow(
                () -> new UserException("User with id {" + userId + "} is not found", HttpStatus.NOT_FOUND)
        );
    }

    @Override
    public void updateIntroduction(
            final User user,
            final String newIntroduction
    ) {
        // 소개 수정 X
        if (newIntroduction == null) {
            return;
        }

        // 소개 기본값 설정
        if (newIntroduction.isEmpty()) {
            user.updateIntroduction(null);
            return;
        }

        // 소개 업데이트
        user.updateIntroduction(newIntroduction);

        // 엘라스틱서치 데이터 조회
        Long userId = user.getId();
        UserSearch userSearch = findUserSearch(userId);

        userSearch.updateIntroduction(newIntroduction);

        // 업데이트
        ElasticsearchUserRollbackContext.addDocumentToUpdate(userSearch);

        // TODO: 현재 유저의 정보를 변경했을 때 ES에 있는 전시회 카드 데이터 업데이트는 반영되지 않고있음
        // 해당 문제해결을 위해서 CDC 구축 ?
    }

    @Override
    public void updateProfileImage(
            final User user,
            final String newProfileImage
    ) {
        // 프로파일 이미지 업데이트
        user.updateProfileImage(newProfileImage);

        // 엘라스틱서치 데이터 조회
        Long userId = user.getId();
        UserSearch userSearch = findUserSearch(userId);

        // 엘라스틱서치 데이터 업데이트
        userSearch.updateProfileImage(newProfileImage);

        // 업데이트
        ElasticsearchUserRollbackContext.addDocumentToUpdate(userSearch);
    }

    @Override
    public Page<UserSearch> searchUsers(
            final String keyword,
            final Pageable pageable
    ) {
        if (keyword.isEmpty()) {
            throw new UserException("No users found.", HttpStatus.NOT_FOUND);
        }

        Page<UserSearch> userSearchPage = userSearchRepository.search(keyword, pageable);

        if (userSearchPage.getTotalElements() == 0) {
            throw new UserException("No users found.", HttpStatus.NOT_FOUND);
        }

        return userSearchPage;
    }

    @Override
    public void deleteUser(final User user) {
        userRepository.delete(user);

        // 엘라스틱 서치 데이터 삭제
        Long userId = user.getId();
        UserSearch userSearch = userSearchRepository.findById(user.getId()).orElseThrow(
                () -> new UserException("User with id {" + userId + "} is not found", HttpStatus.NOT_FOUND)
        );

        ElasticsearchUserRollbackContext.addDocumentToDelete(userSearch);
    }
}
