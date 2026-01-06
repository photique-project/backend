package com.benchpress200.photique.exhibition.application.query.port.in;

import com.benchpress200.photique.exhibition.application.query.model.BookmarkedExhibitionSearchQuery;
import com.benchpress200.photique.exhibition.application.query.result.BookmarkedExhibitionSearchResult;

public interface SearchBookmarkedExhibitionUseCase {
    BookmarkedExhibitionSearchResult searchBookmarkedExhibition(BookmarkedExhibitionSearchQuery query);
}
