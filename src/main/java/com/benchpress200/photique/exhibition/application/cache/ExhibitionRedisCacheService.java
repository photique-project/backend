package com.benchpress200.photique.exhibition.application.cache;

import com.benchpress200.photique.common.dto.RestPage;
import com.benchpress200.photique.exhibition.domain.ExhibitionDomainService;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionSearchRequest;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExhibitionRedisCacheService implements ExhibitionCacheService {
    private final ExhibitionDomainService exhibitionDomainService;

    @Override
    @Cacheable(
            value = "searchExhibitionPage",
            key = "#pageable.pageNumber", // 페이지 번호를 캐싱 키로 지정, 아무조건없는 검색에서 페이지번호 1 ~ 10 캐싱
            condition = "#pageable.sort.getOrderFor('createdAt') != null and #pageable.sort.getOrderFor('createdAt').direction.name() == 'DESC' and #pageable.pageNumber <= 10 and #exhibitionSearchRequest.target.value.equals('work') and #exhibitionSearchRequest.keywords.isEmpty()" // 키워드 없을 때 초반 페이지만 캐싱
    )
    public Page<ExhibitionSearch> searchExhibitions(
            final ExhibitionSearchRequest exhibitionSearchRequest,
            final Pageable pageable
    ) {
        Target target = exhibitionSearchRequest.getTarget();
        List<String> keywords = exhibitionSearchRequest.getKeywords();
        Page<ExhibitionSearch> exhibitionSearchPage = exhibitionDomainService.searchExhibitions(
                target,
                keywords,
                pageable
        );

        return new RestPage<>(exhibitionSearchPage);
    }
}
