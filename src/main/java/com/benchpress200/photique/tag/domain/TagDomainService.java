package com.benchpress200.photique.tag.domain;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionTag;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;
import com.benchpress200.photique.tag.domain.entity.Tag;
import java.util.List;

public interface TagDomainService {
    // 없는 애들 새로 저장하고 있는애들 조회해야함
    List<Tag> createNewTags(List<String> tagNames);

    List<Tag> findTags(List<SingleWorkTag> singleWorkTags);

    List<Tag> findExhibitionTags(List<ExhibitionTag> exhibitionTags);
}
