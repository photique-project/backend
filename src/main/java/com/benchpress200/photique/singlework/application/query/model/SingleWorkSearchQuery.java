package com.benchpress200.photique.singlework.application.query.model;

import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
@Builder
public class SingleWorkSearchQuery {
    private Target target;
    private String keyword;
    private List<Category> categories;
    private Pageable pageable;
}
