package com.benchpress200.photique.tag.domain;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionTag;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;
import com.benchpress200.photique.tag.domain.entity.Tag;
import com.benchpress200.photique.tag.infrastructure.TagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagDomainServiceImpl implements TagDomainService {

    private final TagRepository tagRepository;

    @Override
    public List<Tag> createNewTags(final List<String> tagNames) {
        // 순회하면서 존재하는 태그는 조회, 존재하지 않는 태그는 저장하고 리스트로 반환
        if (tagNames == null) {
            return null;
        }

        return tagNames.stream()
                .map(tagName -> {
                    if (tagRepository.existsByName(tagName)) {
                        return tagRepository.findByName(tagName);
                    }

                    return tagRepository.save(Tag.builder()
                            .name(tagName)
                            .build()
                    );
                })
                .toList();
    }

    @Override
    public List<Tag> findTags(final List<SingleWorkTag> singleWorkTags) {
        return singleWorkTags.stream()
                .map(SingleWorkTag::getTag)
                .toList();
    }

    @Override
    public List<Tag> findExhibitionTags(final List<ExhibitionTag> exhibitionTags) {
        return exhibitionTags.stream()
                .map(ExhibitionTag::getTag)
                .toList();
    }
}
