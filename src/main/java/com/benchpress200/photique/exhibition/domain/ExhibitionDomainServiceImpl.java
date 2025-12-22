package com.benchpress200.photique.exhibition.domain;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionBookmark;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionLike;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionTag;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionWork;
import com.benchpress200.photique.exhibition.domain.repository.ExhibitionBookmarkRepository;
import com.benchpress200.photique.exhibition.domain.repository.ExhibitionLikeRepository;
import com.benchpress200.photique.exhibition.domain.repository.ExhibitionRepository;
import com.benchpress200.photique.exhibition.domain.repository.ExhibitionSearchRepository;
import com.benchpress200.photique.exhibition.domain.repository.ExhibitionTagRepository;
import com.benchpress200.photique.exhibition.domain.repository.ExhibitionWorkRepository;
import com.benchpress200.photique.exhibition.exception.ExhibitionException;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import com.benchpress200.photique.user.domain.entity.User;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExhibitionDomainServiceImpl implements ExhibitionDomainService {

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionWorkRepository exhibitionWorkRepository;
    private final ExhibitionTagRepository exhibitionTagRepository;
    private final ExhibitionLikeRepository exhibitionLikeRepository;
    private final ExhibitionBookmarkRepository exhibitionBookmarkRepository;
    private final ExhibitionSearchRepository exhibitionSearchRepository;

    @Override
    public Exhibition findExhibitionWithWorksAndWriter(@Param("id") Long id) {
        return exhibitionRepository.findWithWorksAndWriter(id).orElseThrow(
                () -> new ExhibitionException("Exhibition with id [" + id + "] is not found.",
                        HttpStatus.NOT_FOUND)
        );
    }

    @Override
    public List<Exhibition> findExhibition(User user) {
        return exhibitionRepository.findByWriter(user);
    }

    @Override
    public List<ExhibitionWork> findExhibitionWork(Exhibition exhibition) {
        return exhibitionWorkRepository.findByExhibition(exhibition);
    }

    @Override
    public void deleteLike(User user) {
        exhibitionLikeRepository.deleteByUser(user);
    }

    @Override
    public void deleteBookmark(User user) {
        exhibitionBookmarkRepository.deleteByUser(user);
    }

    @Override
    public void deleteExhibition(Exhibition exhibition) {
        exhibitionRepository.delete(exhibition);

        // 엘라스틱서치 데이터 삭제
        Long exhibitionId = exhibition.getId();
        ExhibitionSearch exhibitionSearch = findExhibitionSearch(exhibitionId);
    }

    @Override
    public void deleteLike(Exhibition exhibition) {
        exhibitionLikeRepository.deleteByExhibition(exhibition);
    }

    @Override
    public void deleteBookmark(Exhibition exhibition) {
        exhibitionBookmarkRepository.deleteByExhibition(exhibition);
    }

    @Override
    public void deleteExhibitionWork(Exhibition exhibition) {
        exhibitionWorkRepository.deleteByExhibition(exhibition);
    }

    @Override
    public void deleteExhibitionWork(ExhibitionWork exhibitionWork) {
        exhibitionWorkRepository.delete(exhibitionWork);
    }

    @Override
    public Exhibition createNewExhibition(Exhibition exhibition) {
        return exhibitionRepository.save(exhibition);
    }

    @Override
    public void createNewExhibitionWorks(List<ExhibitionWork> exhibitionWorks) {
        exhibitionWorkRepository.saveAll(exhibitionWorks);
    }

    @Override
    public void createNewExhibitionTags(List<ExhibitionTag> exhibitionTags) {
        exhibitionTagRepository.saveAll(exhibitionTags);
    }

    @Override
    public void createNewExhibitionSearch(ExhibitionSearch exhibitionSearch) {
        
    }

    @Override
    public Exhibition findExhibition(Long exhibitionId) {
        return exhibitionRepository.findById(exhibitionId).orElseThrow(
                () -> new ExhibitionException("Exhibition with id [" + exhibitionId + "] is not found.",
                        HttpStatus.NOT_FOUND)
        );
    }

    @Override
    public List<ExhibitionWork> findExhibitionWorks(Exhibition exhibition) {
        return exhibitionWorkRepository.findByExhibition(exhibition);
    }

    @Override
    public void incrementView(Exhibition exhibition) {
        exhibition.incrementView();
    }

    private ExhibitionSearch findExhibitionSearch(Long exhibitionId) {
        return exhibitionSearchRepository.findById(exhibitionId).orElseThrow(
                () -> new ExhibitionException("Exhibition with ID " + exhibitionId + " is not found.",
                        HttpStatus.NOT_FOUND)
        );
    }

    @Override
    public Page<ExhibitionSearch> searchExhibitions(
            Target target,
            List<String> keywords,
            Pageable pageable
    ) {
        Page<ExhibitionSearch> exhibitionSearchPage = exhibitionSearchRepository.searchExhibitions(
                target,
                keywords,
                pageable
        );

        if (exhibitionSearchPage.getTotalElements() == 0) {
            throw new ExhibitionException("Exhibitions is not found.", HttpStatus.NOT_FOUND);
        }

        return exhibitionSearchPage;
    }

    @Override
    public void deleteExhibitionLike(Exhibition exhibition) {
        exhibitionLikeRepository.deleteByExhibition(exhibition);
    }

    @Override
    public void deleteExhibitionBookmark(Exhibition exhibition) {
        exhibitionBookmarkRepository.deleteByExhibition(exhibition);
    }

    @Override
    public void deleteExhibitionTag(Exhibition exhibition) {
        exhibitionTagRepository.deleteByExhibition(exhibition);
    }

    @Override
    public void isAlreadyLiked(
            User user,
            Exhibition exhibition
    ) {
        if (exhibitionLikeRepository.existsByUserAndExhibition(user, exhibition)) {
            throw new ExhibitionException("User has already liked this exhibition", HttpStatus.CONFLICT);
        }
    }

    @Override
    public void incrementLike(ExhibitionLike exhibitionLike) {
        exhibitionLikeRepository.save(exhibitionLike);
    }

    @Override
    public void decrementLike(
            User user,
            Exhibition exhibition
    ) {
        exhibitionLikeRepository.deleteByUserAndExhibition(user, exhibition);
    }

    @Override
    public void isAlreadyBookmarked(
            User user,
            Exhibition exhibition
    ) {

        if (exhibitionBookmarkRepository.existsByUserAndExhibition(user, exhibition)) {
            throw new ExhibitionException("User has already marked this exhibition", HttpStatus.CONFLICT);
        }
    }

    @Override
    public void addBookmark(ExhibitionBookmark exhibitionBookmark) {
        exhibitionBookmarkRepository.save(exhibitionBookmark);
    }

    @Override
    public void removeBookmark(
            User user,
            Exhibition exhibition
    ) {
        exhibitionBookmarkRepository.deleteByUserAndExhibition(user, exhibition);
    }

    @Override
    public Long countExhibition(User user) {
        return exhibitionRepository.countByWriter(user);
    }

    @Override
    public boolean isLiked(
            Long userId,
            Long exhibitionId
    ) {
        if (userId == 0) {
            return false;
        }

        return exhibitionLikeRepository.existsByUserIdAndExhibitionId(userId, exhibitionId);
    }

    @Override
    public boolean isBookmarked(
            Long userId,
            Long exhibitionId
    ) {
        if (userId == null) {
            return false;
        }

        return exhibitionBookmarkRepository.existsByUserIdAndExhibitionId(userId, exhibitionId);
    }

    @Override
    public Page<ExhibitionSearch> findBookmarkedExhibitionsByUser(
            Long userId,
            Pageable pageable
    ) {
        // 유저가 북마크한 전시회 페이지 조회
        Page<ExhibitionBookmark> exhibitionBookmarkPage = exhibitionBookmarkRepository.findByUserId(userId, pageable);

        // 전시회 아이디에 해당하는 전시회 조회
        List<ExhibitionSearch> exhibitionSearches = exhibitionBookmarkPage.stream()
                .map(exhibitionBookmark -> exhibitionSearchRepository.findById(
                        exhibitionBookmark.getExhibition().getId()).orElseThrow(
                        () -> new ExhibitionException("Exhibition not found", HttpStatus.NOT_FOUND)
                ))
                .toList();

        return new PageImpl<>(exhibitionSearches, pageable, exhibitionBookmarkPage.getTotalElements());
    }

    @Override
    public Page<ExhibitionSearch> findLikedExhibitionsByUser(
            Long userId,
            Pageable pageable
    ) {
        // 유저가 좋아요한 전시회 페이지 조회
        Page<ExhibitionLike> exhibitionLikePage = exhibitionLikeRepository.findByUserId(userId, pageable);

        // 전시회 아이디에 해당하는 전시회 조회
        List<ExhibitionSearch> exhibitionSearches = exhibitionLikePage.stream()
                .map(exhibitionBookmark -> exhibitionSearchRepository.findById(
                        exhibitionBookmark.getExhibition().getId()).orElseThrow(
                        () -> new ExhibitionException("Exhibition not found", HttpStatus.NOT_FOUND)
                ))
                .toList();

        return new PageImpl<>(exhibitionSearches, pageable, exhibitionLikePage.getTotalElements());
    }

    @Override
    public Page<ExhibitionSearch> findMyExhibitions(
            Long userId,
            Pageable pageable
    ) {
        Page<ExhibitionSearch> exhibitionSearchPage = exhibitionSearchRepository.findByWriterId(userId, pageable);

        if (exhibitionSearchPage.getTotalElements() == 0) {
            throw new ExhibitionException("Exhibition not found", HttpStatus.NOT_FOUND);
        }

        return exhibitionSearchPage;
    }

    @Override
    public List<ExhibitionSearch> findExhibitionSearchesByWriterId(long id) {
        return exhibitionSearchRepository.findAllByWriterId(id);
    }

    @Override
    public void updateAllExhibitionSearch(List<ExhibitionSearch> exhibitionSearches) {
        exhibitionSearchRepository.saveAll(exhibitionSearches);
    }

    @Override
    public void markAsUpdated(Exhibition exhibition) {
        exhibition.markAsUpdated();
    }

    @Override
    public List<Exhibition> findExhibitionsModifiedSince(LocalDateTime time) {
        return exhibitionRepository.findAllByUpdatedAtAfter(time);
    }

    @Override
    public List<ExhibitionTag> findExhibitionTag(Exhibition exhibition) {
        return exhibitionTagRepository.findByExhibition(exhibition);
    }

    @Override
    public long countLike(Exhibition exhibition) {
        return exhibitionLikeRepository.countByExhibition(exhibition);
    }

    @Override
    public Set<Long> findLikedExhibitionIds(
            long userId,
            List<Long> exhibitionIds
    ) {
        if (userId == 0) {
            return new HashSet<>();
        }

        List<Long> likedIds = exhibitionLikeRepository.findLikedExhibitionIdsByUserIdAndExhibitionIds(
                userId,
                exhibitionIds
        );

        return new HashSet<>(likedIds);
    }

    @Override
    public Set<Long> findBookmarkedExhibitionIds(
            long userId,
            List<Long> exhibitionIds
    ) {
        if (userId == 0) {
            return new HashSet<>();
        }

        List<Long> bookmarkedIds = exhibitionBookmarkRepository.findBookmarkedExhibitionIdsByUserIdAndExhibitionIds(
                userId,
                exhibitionIds
        );

        return new HashSet<>(bookmarkedIds);
    }
}
