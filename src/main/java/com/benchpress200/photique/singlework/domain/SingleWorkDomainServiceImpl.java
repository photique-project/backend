package com.benchpress200.photique.singlework.domain;

import com.benchpress200.photique.common.transaction.rollbackcontext.ElasticsearchSingleWorkRollbackContext;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkLike;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;
import com.benchpress200.photique.singlework.domain.enumeration.Aperture;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.ISO;
import com.benchpress200.photique.singlework.domain.enumeration.ShutterSpeed;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import com.benchpress200.photique.singlework.exception.SingleWorkException;
import com.benchpress200.photique.singlework.infrastructure.SingleWorkLikeRepository;
import com.benchpress200.photique.singlework.infrastructure.SingleWorkRepository;
import com.benchpress200.photique.singlework.infrastructure.SingleWorkSearchRepository;
import com.benchpress200.photique.singlework.infrastructure.SingleWorkTagRepository;
import com.benchpress200.photique.tag.domain.entity.Tag;
import com.benchpress200.photique.user.domain.entity.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SingleWorkDomainServiceImpl implements SingleWorkDomainService {
    private final SingleWorkRepository singleWorkRepository;
    private final SingleWorkLikeRepository singleWorkLikeRepository;
    private final SingleWorkTagRepository singleWorkTagRepository;
    private final SingleWorkSearchRepository singleWorkSearchRepository;

    @Override
    public List<SingleWork> findSingleWork(final User writer) {
        return singleWorkRepository.findByWriter(writer);
    }

    @Override
    public SingleWork findSingleWork(Long id) {
        return singleWorkRepository.findById(id).orElseThrow(
                () -> new SingleWorkException("Single work with id " + id + " is not found.",
                        HttpStatus.NOT_FOUND)
        );
    }

    @Override
    public void deleteLike(final User user) {
        singleWorkLikeRepository.deleteByUser(user);
    }

    @Override
    public void deleteSingleWork(final SingleWork singleWork) {
        singleWorkRepository.delete(singleWork);

        // 엘라스틱 서치 삭제
        Long singleWorkId = singleWork.getId();
        SingleWorkSearch singleWorkSearch = singleWorkSearchRepository.findById(singleWorkId).orElseThrow(
                () -> new SingleWorkException("Single work with id " + singleWorkId + " is not found.",
                        HttpStatus.NOT_FOUND)
        );

        ElasticsearchSingleWorkRollbackContext.addDocumentToDelete(singleWorkSearch);
    }

    @Override
    public void deleteLike(final SingleWork singleWork) {
        singleWorkLikeRepository.deleteBySingleWork(singleWork);
    }

    @Override
    public SingleWork createNewSingleWork(final SingleWork singleWork) {
        return singleWorkRepository.save(singleWork);
    }

    @Override
    public void createNewSingleWorkTags(final List<SingleWorkTag> singleWorkTags) {
        singleWorkTagRepository.saveAll(singleWorkTags);
    }

    @Override
    public void createNewSingleWorkSearch(final SingleWorkSearch singleWorkSearch) {
        ElasticsearchSingleWorkRollbackContext.addDocumentToSave(singleWorkSearch);
    }

    @Override
    public void incrementView(final SingleWork singleWork) {
        singleWork.incrementView();
    }

    @Override
    public List<SingleWorkTag> findSingleWorkTag(final SingleWork singleWork) {
        return singleWorkTagRepository.findBySingleWork(singleWork);
    }

    @Override
    public Page<SingleWorkSearch> searchSingleWorks(
            final Target target,
            final List<String> keywords,
            final List<Category> categories,
            final Pageable pageable
    ) {
        Page<SingleWorkSearch> singleWorkSearchPage = singleWorkSearchRepository.search(
                target,
                keywords,
                categories,
                pageable
        );

        if (singleWorkSearchPage.getTotalElements() == 0) {
            throw new SingleWorkException("No single works found.", HttpStatus.NOT_FOUND);
        }

        return singleWorkSearchPage;
    }

    @Override
    public void updateImage(
            final SingleWork singleWork,
            final String uploadedNewImageUrl
    ) {
        // 이미지 업데이트
        singleWork.updateImage(uploadedNewImageUrl);
    }

    @Override
    public void updateCamera(final SingleWork singleWork, final String newCamera) {
        // 카메라  값이 null이면 수정 x
        if (newCamera == null) {
            return;
        }

        singleWork.updateCamera(newCamera);
    }

    @Override
    public void updateLens(
            final SingleWork singleWork,
            final String newLens
    ) {
        if (newLens == null) {
            return;
        }

        // 빈 값이면 기본값으로 업데이트 이므로 null
        if (newLens.isEmpty()) {
            singleWork.updateLens(null);
            return;
        }

        singleWork.updateLens(newLens);
    }

    @Override
    public void updateAperture(
            final SingleWork singleWork,
            final String newAperture
    ) {
        if (newAperture == null) {
            return;
        }

        if (newAperture.isEmpty()) {
            singleWork.updateLens(null);
            return;
        }

        Aperture aperture = Aperture.fromValue(newAperture);
        singleWork.updateAperture(aperture);
    }

    @Override
    public void updateShutterSpeed(
            final SingleWork singleWork,
            final String newShutterSpeed
    ) {
        if (newShutterSpeed == null) {
            return;
        }

        if (newShutterSpeed.isEmpty()) {
            singleWork.updateShutterSpeed(null);
            return;
        }

        ShutterSpeed shutterSpeed = ShutterSpeed.fromValue(newShutterSpeed);
        singleWork.updateShutterSpeed(shutterSpeed);
    }

    @Override
    public void updateIso(
            final SingleWork singleWork,
            final String newIso
    ) {
        if (newIso == null) {
            return;
        }

        if (newIso.isEmpty()) {
            singleWork.updateIso(null);
            return;
        }

        ISO iso = ISO.fromValue(newIso);
        singleWork.updateIso(iso);
    }

    @Override
    public void updateLocation(
            final SingleWork singleWork,
            final String newLocation
    ) {
        if (newLocation == null) {
            return;
        }

        if (newLocation.isEmpty()) {
            singleWork.updateLocation(null);
            return;
        }

        singleWork.updateLocation(newLocation);
    }

    @Override
    public void updateCategory(
            final SingleWork singleWork,
            final String newCategory
    ) {
        if (newCategory == null) {
            return;
        }

        if (newCategory.isEmpty()) {
            throw new SingleWorkException("Invalid category", HttpStatus.BAD_REQUEST);
        }

        Category category = Category.fromValue(newCategory);
        singleWork.updateCategory(category);
    }

    @Override
    public void updateDate(
            final SingleWork singleWork,
            final LocalDate newDate
    ) {
        if (newDate == null) {
            return;
        }
        // 필수값이라서 빈 값으로 기본값 설정이 불가능하지만 요청 DTO 검증 어노테이션으로 해결가능
        singleWork.updateDate(newDate);
    }

    @Override
    public void updateTags(
            final SingleWork singleWork,
            final List<Tag> tags
    ) {
        // 기존 태그 유지
        if (tags == null) {
            return;
        }

        // 기존 싱글워크 태그삭제
        List<SingleWorkTag> singleWorkTags = tags.stream()
                .map(tag -> SingleWorkTag.builder()
                        .singleWork(singleWork)
                        .tag(tag)
                        .build()
                )
                .toList();

        singleWorkTagRepository.deleteBySingleWork(singleWork);
        singleWorkTagRepository.saveAll(singleWorkTags);
    }

    @Override
    public void updateTitle(
            final SingleWork singleWork,
            final String newTitle
    ) {
        if (newTitle == null) {
            return;
        }

        singleWork.updateTitle(newTitle);
    }

    @Override
    public void updateDescription(
            final SingleWork singleWork,
            final String newDescription
    ) {
        if (newDescription == null) {
            return;
        }

        singleWork.updateDescription(newDescription);
    }

    @Override
    public Long countLike(final SingleWork singleWork) {
        return singleWorkLikeRepository.countBySingleWork(singleWork);
    }

    @Override
    public void deleteSingleWorkLike(final SingleWork singleWork) {
        singleWorkLikeRepository.deleteBySingleWork(singleWork);
    }

    @Override
    public void deleteSingleWorkTag(final SingleWork singleWork) {
        singleWorkTagRepository.deleteBySingleWork(singleWork);
    }

    @Override
    public void incrementLike(final SingleWorkLike singleWorkLike) {
        singleWorkLikeRepository.save(singleWorkLike);
    }

    @Override
    public void isAlreadyLiked(
            final User user,
            final SingleWork singleWork
    ) {
        if (singleWorkLikeRepository.existsByUserAndSingleWork(user, singleWork)) {
            throw new SingleWorkException("You have already liked this single work.", HttpStatus.CONFLICT);
        }
    }

    @Override
    public void decrementLike(
            final User user,
            final SingleWork singleWork
    ) {
        singleWorkLikeRepository.deleteByUserAndSingleWork(user, singleWork);
    }

    @Override
    public Long countSingleWork(final User user) {
        return singleWorkRepository.countByWriter(user);
    }

    @Override
    public SingleWork findPopularSingleWork() {
        // 이번주 동안
        SingleWork singleWork = singleWorkRepository.findPopularSingleWork();
        if (singleWork == null) {
            throw new SingleWorkException("No single work found.", HttpStatus.NOT_FOUND);
        }

        return singleWork;
    }

    @Override
    public List<SingleWorkLike> findLikeByUser(final Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        return singleWorkLikeRepository.findByUserId(userId);
    }

    @Override
    public boolean isLiked(
            final Long userId,
            final Long singleWorkId
    ) {
        // 로그인상태가 아니라면 userId 0
        if (userId == 0) {
            return false;
        }

        return singleWorkLikeRepository.existsByUserIdAndSingleWorkId(userId, singleWorkId);
    }

    @Override
    public Page<SingleWorkSearch> findLikedSingleWorksByUser(
            final Long userId,
            final Pageable pageable
    ) {
        // 유저가 좋아요한 작품 페이지 기준으로 조회
        Page<SingleWorkLike> singleWorkLikePage = singleWorkLikeRepository.findByUserId(userId, pageable);

        List<SingleWorkSearch> singleWorkSearches = singleWorkLikePage.stream()
                .map(singleWorkLike -> singleWorkSearchRepository.findById(
                                singleWorkLike.getSingleWork().getId()).orElseThrow(
                                () -> new SingleWorkException("Single Work not found", HttpStatus.NOT_FOUND)
                        )
                ).toList();

        return new PageImpl<>(singleWorkSearches, pageable, singleWorkLikePage.getTotalElements());
    }

    @Override
    public Page<SingleWorkSearch> findMySingleWorkByUser(
            final Long userId,
            final Pageable pageable
    ) {
        Page<SingleWorkSearch> singleWorkSearchPage = singleWorkSearchRepository.findByWriterId(userId, pageable);

        if (singleWorkSearchPage.getTotalElements() == 0) {
            throw new SingleWorkException("Single Work not found", HttpStatus.NOT_FOUND);
        }

        return singleWorkSearchPage;
    }

    @Override
    public List<SingleWork> findSingleWorksModifiedSince(final LocalDateTime time) {
        return singleWorkRepository.findAllByUpdatedAtAfter(time);
    }

    @Override
    public List<SingleWorkSearch> findSingleWorkSearchesByWriterId(final Long id) {
        return singleWorkSearchRepository.findAllByWriterId(id);
    }

    @Override
    public void updateAllSingleWorkSearch(final List<SingleWorkSearch> singleWorkSearches) {
        singleWorkSearchRepository.saveAll(singleWorkSearches);
    }

    @Override
    public void markAsUpdated(final SingleWork singleWork) {
        singleWork.markAsUpdated();
    }

    @Override
    public Set<Long> findLikedSingleWorkIds(
            final Long userId,
            final List<Long> singleWorkIds
    ) {
        if (userId == 0) {
            return new HashSet<>();
        }

        List<Long> likedIds = singleWorkLikeRepository.findLikedSingleWorkIdsByUserIdAndSingleWorkIds(
                userId,
                singleWorkIds
        );
        return new HashSet<>(likedIds);
    }
}
