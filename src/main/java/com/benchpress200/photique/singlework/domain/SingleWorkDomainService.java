package com.benchpress200.photique.singlework.domain;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkLike;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import com.benchpress200.photique.tag.domain.entity.Tag;
import com.benchpress200.photique.user.domain.entity.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SingleWorkDomainService {
    List<SingleWork> findSingleWork(User writer);

    SingleWork findSingleWork(Long id);

    void deleteLike(User user);

    void deleteLike(SingleWork singleWork);

    void deleteSingleWork(SingleWork singleWork);

    SingleWork createNewSingleWork(SingleWork singleWork);

    void createNewSingleWorkTags(List<SingleWorkTag> singleWorkTags);

    void createNewSingleWorkSearch(SingleWorkSearch singleWorkSearch);

    void incrementView(SingleWork singleWork);

    List<SingleWorkTag> findSingleWorkTag(SingleWork singleWork);

    Page<SingleWorkSearch> searchSingleWorks(Target target, List<String> keywords, List<Category> categories,
                                             Pageable pageable);

    void updateImage(SingleWork singleWork, String uploadedNewImageUrl);

    void updateCamera(SingleWork singleWork, String newCamera);

    void updateLens(SingleWork singleWork, String newLens);

    void updateAperture(SingleWork singleWork, String newAperture);

    void updateShutterSpeed(SingleWork singleWork, String newShutterSpeed);

    void updateIso(SingleWork singleWork, String newIso);

    void updateLocation(SingleWork singleWork, String newLocation);

    void updateCategory(SingleWork singleWork, String newCategory);

    void updateDate(SingleWork singleWork, LocalDate newDate);

    void updateTags(SingleWork singleWork, List<Tag> singleWorkTags);

    void updateTitle(SingleWork singleWork, String newTitle);

    void updateDescription(SingleWork singleWork, String newDescription);

    void deleteSingleWorkLike(SingleWork singleWork);

    Long countLike(SingleWork singleWork);

    void deleteSingleWorkTag(SingleWork singleWork);

    void incrementLike(SingleWorkLike singleWorkLike);

    void isAlreadyLiked(User user, SingleWork singleWork);

    void decrementLike(User user, SingleWork singleWork);

    Long countSingleWork(User user);

    SingleWork findPopularSingleWork();

    List<SingleWorkLike> findLikeByUser(Long userId);

    boolean isLiked(Long userId, Long singleWorkId);

    Page<SingleWorkSearch> findLikedSingleWorksByUser(Long userId, Pageable pageable);

    Page<SingleWorkSearch> findMySingleWorkByUser(Long userId, Pageable pageable);

    List<SingleWork> findSingleWorksModifiedSince(LocalDateTime time);

    List<SingleWorkSearch> findSingleWorkSearchesByWriterId(Long id);

    void updateAllSingleWorkSearch(List<SingleWorkSearch> singleWorkSearches);

    void markAsUpdated(SingleWork singleWork);
}
