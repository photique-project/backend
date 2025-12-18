package com.benchpress200.photique.singlework.application.cache;

import com.benchpress200.photique.common.dto.RestPage;
import com.benchpress200.photique.singlework.domain.SingleWorkDomainService;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkSearchRequest;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SingleWorkRedisCacheService implements SingleWorkCacheService {
    private final SingleWorkDomainService singleWorkDomainService;

    @Override
    @Cacheable(
            value = "searchSingleWorkPage",
            key = "#pageable.pageNumber", // 페이지 번호를 캐싱 키로 지정, 아무조건없는 검색에서 페이지번호 1 ~ 10 캐싱
            condition = "#pageable.sort.getOrderFor('createdAt') != null and #pageable.sort.getOrderFor('createdAt').direction.name() == 'DESC' and #pageable.pageNumber <= 10 and #singleWorkSearchRequest.target.value.equals('work') and #singleWorkSearchRequest.keywords.isEmpty() and #singleWorkSearchRequest.categories.isEmpty()" // 키워드 없을 때 초반 페이지만 캐싱
    )
    public Page<SingleWorkSearch> searchSingleWorks(
            SingleWorkSearchRequest singleWorkSearchRequest,
            Pageable pageable
    ) {
        // 검색조건
        Target target = singleWorkSearchRequest.getTarget();
        List<String> keywords = singleWorkSearchRequest.getKeywords();
        List<Category> categories = singleWorkSearchRequest.getCategories();
        Page<SingleWorkSearch> singleWorkSearchPage = singleWorkDomainService.searchSingleWorks(
                target,
                keywords,
                categories,
                pageable
        );

        return new RestPage<>(singleWorkSearchPage);
    }
}
