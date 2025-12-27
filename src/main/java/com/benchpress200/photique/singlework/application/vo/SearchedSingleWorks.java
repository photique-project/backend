package com.benchpress200.photique.singlework.application.vo;

import com.benchpress200.photique.singlework.application.result.SearchedSingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;

public class SearchedSingleWorks {
    private final List<SearchedSingleWork> searchedSingleWorks;

    private SearchedSingleWorks(List<SearchedSingleWork> searchedSingleWorks) {
        this.searchedSingleWorks = searchedSingleWorks;
    }

    public static SearchedSingleWorks of(
            Page<SingleWorkSearch> singleWorkSearchPage,
            LikedSingleWorkIds likedSingleWorkIds
    ) {
        List<SearchedSingleWork> searchedSingleWorks = singleWorkSearchPage.stream()
                .map(singleWorkSearch -> {
                    Long singleWorkId = singleWorkSearch.getId();
                    boolean isLiked = likedSingleWorkIds.contains(singleWorkId);

                    return SearchedSingleWork.of(singleWorkSearch, isLiked);
                })
                .toList();

        return new SearchedSingleWorks(searchedSingleWorks);
    }

    public List<SearchedSingleWork> values() {
        return new ArrayList<>(searchedSingleWorks);
    }
}
