package com.benchpress200.photique.exhibition.domain;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionBookmark;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionLike;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionTag;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionWork;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExhibitionDomainService {
    List<Exhibition> findExhibition(User user);

    List<ExhibitionWork> findExhibitionWork(Exhibition exhibition);

    void deleteLike(User user);

    void deleteLike(Exhibition exhibition);

    void deleteBookmark(User user);

    void deleteBookmark(Exhibition exhibition);

    void deleteExhibition(Exhibition exhibition);

    void deleteExhibitionWork(Exhibition exhibition);

    void deleteExhibitionWork(ExhibitionWork exhibitionWork);

    Exhibition createNewExhibition(Exhibition exhibition);

    void createNewExhibitionWorks(List<ExhibitionWork> exhibitionWorks);

    void createNewExhibitionTags(List<ExhibitionTag> exhibitionTags);

    void createNewExhibitionSearch(ExhibitionSearch exhibitionSearch);

    Exhibition findExhibition(final Long exhibitionId);

    List<ExhibitionWork> findExhibitionWorks(Exhibition exhibition);

    void incrementView(final Exhibition exhibition);

    Page<ExhibitionSearch> searchExhibitions(Target target, List<String> keywords, Pageable pageable);

    void deleteExhibitionLike(Exhibition exhibition);

    void deleteExhibitionBookmark(Exhibition exhibition);

    void deleteExhibitionTag(Exhibition exhibition);

    void isLiked(User user, Exhibition exhibition);

    void incrementLike(ExhibitionLike exhibitionLike);

    void decrementLike(User user, Exhibition exhibition);

    void isBookmarked(User user, Exhibition exhibition);

    void addBookmark(ExhibitionBookmark exhibitionBookmark);

    void removeBookmark(User user, Exhibition exhibition);
}
