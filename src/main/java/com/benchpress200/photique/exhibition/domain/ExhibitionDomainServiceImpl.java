package com.benchpress200.photique.exhibition.domain;

import com.benchpress200.photique.common.transaction.rollbackcontext.ElasticsearchExhibitionRollbackContext;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionBookmark;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionLike;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionTag;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionWork;
import com.benchpress200.photique.exhibition.exception.ExhibitionException;
import com.benchpress200.photique.exhibition.infrastructure.ExhibitionBookmarkRepository;
import com.benchpress200.photique.exhibition.infrastructure.ExhibitionLikeRepository;
import com.benchpress200.photique.exhibition.infrastructure.ExhibitionRepository;
import com.benchpress200.photique.exhibition.infrastructure.ExhibitionSearchRepository;
import com.benchpress200.photique.exhibition.infrastructure.ExhibitionTagRepository;
import com.benchpress200.photique.exhibition.infrastructure.ExhibitionWorkRepository;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public List<Exhibition> findExhibition(final User user) {
        return exhibitionRepository.findByWriter(user);
    }

    @Override
    public List<ExhibitionWork> findExhibitionWork(final Exhibition exhibition) {
        return exhibitionWorkRepository.findByExhibition(exhibition);
    }

    @Override
    public void deleteLike(final User user) {
        exhibitionLikeRepository.deleteByUser(user);
    }

    @Override
    public void deleteBookmark(final User user) {
        exhibitionBookmarkRepository.deleteByUser(user);
    }

    @Override
    public void deleteExhibition(final Exhibition exhibition) {
        exhibitionRepository.delete(exhibition);

        // 엘라스틱서치 데이터 삭제
        Long exhibitionId = exhibition.getId();
        ExhibitionSearch exhibitionSearch = findExhibitionSearch(exhibitionId);
        ElasticsearchExhibitionRollbackContext.addDocumentToDelete(exhibitionSearch);
    }

    @Override
    public void deleteLike(final Exhibition exhibition) {
        exhibitionLikeRepository.deleteByExhibition(exhibition);
    }

    @Override
    public void deleteBookmark(final Exhibition exhibition) {
        exhibitionBookmarkRepository.deleteByExhibition(exhibition);
    }

    @Override
    public void deleteExhibitionWork(final Exhibition exhibition) {
        exhibitionWorkRepository.deleteByExhibition(exhibition);
    }

    @Override
    public void deleteExhibitionWork(final ExhibitionWork exhibitionWork) {
        exhibitionWorkRepository.delete(exhibitionWork);
    }

    @Override
    public Exhibition createNewExhibition(final Exhibition exhibition) {
        return exhibitionRepository.save(exhibition);
    }

    @Override
    public void createNewExhibitionWorks(final List<ExhibitionWork> exhibitionWorks) {
        exhibitionWorkRepository.saveAll(exhibitionWorks);
    }

    @Override
    public void createNewExhibitionTags(final List<ExhibitionTag> exhibitionTags) {
        exhibitionTagRepository.saveAll(exhibitionTags);
    }

    @Override
    public void createNewExhibitionSearch(final ExhibitionSearch exhibitionSearch) {
        ElasticsearchExhibitionRollbackContext.addDocumentToSave(exhibitionSearch);
    }

    @Override
    public Exhibition findExhibition(final Long exhibitionId) {
        return exhibitionRepository.findById(exhibitionId).orElseThrow(
                () -> new ExhibitionException("Exhibition with id [" + exhibitionId + "] is not found.",
                        HttpStatus.NOT_FOUND)
        );
    }

    @Override
    public List<ExhibitionWork> findExhibitionWorks(final Exhibition exhibition) {
        return exhibitionWorkRepository.findByExhibition(exhibition);
    }

    @Override
    public void incrementView(final Exhibition exhibition) {
        exhibition.incrementView();

        // 엘라스틱서치 데이터 업데이트
        Long exhibitionId = exhibition.getId();
        ExhibitionSearch exhibitionSearch = findExhibitionSearch(exhibitionId);

        exhibitionSearch.incrementViewCount();
        ElasticsearchExhibitionRollbackContext.addDocumentToUpdate(exhibitionSearch);
    }

    private ExhibitionSearch findExhibitionSearch(final Long exhibitionId) {
        if (ElasticsearchExhibitionRollbackContext.hasDocumentToUpdate()) {
            return ElasticsearchExhibitionRollbackContext.getDocumentToUpdate();
        }

        return exhibitionSearchRepository.findById(exhibitionId).orElseThrow(
                () -> new ExhibitionException("Exhibition with ID " + exhibitionId + " is not found.",
                        HttpStatus.NOT_FOUND)
        );
    }

    @Override
    public Page<ExhibitionSearch> searchExhibitions(
            final Target target,
            final List<String> keywords,
            final Pageable pageable
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
    public void deleteExhibitionLike(final Exhibition exhibition) {
        exhibitionLikeRepository.deleteByExhibition(exhibition);
    }

    @Override
    public void deleteExhibitionBookmark(final Exhibition exhibition) {
        exhibitionBookmarkRepository.deleteByExhibition(exhibition);
    }

    @Override
    public void deleteExhibitionTag(final Exhibition exhibition) {
        exhibitionTagRepository.deleteByExhibition(exhibition);
    }

    @Override
    public void isLiked(
            final User user,
            final Exhibition exhibition
    ) {
        if (exhibitionLikeRepository.existsByUserAndExhibition(user, exhibition)) {
            throw new ExhibitionException("User has already liked this exhibition", HttpStatus.CONFLICT);
        }
    }

    @Override
    public void incrementLike(final ExhibitionLike exhibitionLike) {
        exhibitionLikeRepository.save(exhibitionLike);

        // 엘라스틱서치 데이터 업데이트
        Exhibition exhibition = exhibitionLike.getExhibition();
        Long exhibitionId = exhibition.getId();
        Long likeCount = exhibitionLikeRepository.countByExhibition(exhibition);
        ExhibitionSearch exhibitionSearch = findExhibitionSearch(exhibitionId);
        exhibitionSearch.updateLikeCount(likeCount);
        ElasticsearchExhibitionRollbackContext.addDocumentToUpdate(exhibitionSearch);
    }

    @Override
    public void decrementLike(
            final User user,
            final Exhibition exhibition
    ) {
        exhibitionLikeRepository.deleteByUserAndExhibition(user, exhibition);

        // 엘라스틱서치 데이터 업데이트
        Long exhibitionId = exhibition.getId();
        Long likeCount = exhibitionLikeRepository.countByExhibition(exhibition);
        ExhibitionSearch exhibitionSearch = findExhibitionSearch(exhibitionId);
        exhibitionSearch.updateLikeCount(likeCount);
        ElasticsearchExhibitionRollbackContext.addDocumentToUpdate(exhibitionSearch);
    }

    @Override
    public void isBookmarked(
            final User user,
            final Exhibition exhibition
    ) {

        if (exhibitionBookmarkRepository.existsByUserAndExhibition(user, exhibition)) {
            throw new ExhibitionException("User has already marked this exhibition", HttpStatus.CONFLICT);
        }
    }

    @Override
    public void addBookmark(final ExhibitionBookmark exhibitionBookmark) {
        exhibitionBookmarkRepository.save(exhibitionBookmark);
    }

    @Override
    public void removeBookmark(
            final User user,
            final Exhibition exhibition
    ) {
        exhibitionBookmarkRepository.deleteByUserAndExhibition(user, exhibition);
    }
}
