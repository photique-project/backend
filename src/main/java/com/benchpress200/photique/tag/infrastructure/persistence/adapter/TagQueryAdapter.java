package com.benchpress200.photique.tag.infrastructure.persistence.adapter;

import com.benchpress200.photique.tag.domain.entity.Tag;
import com.benchpress200.photique.tag.domain.port.TagQueryPort;
import com.benchpress200.photique.tag.infrastructure.persistence.jpa.TagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TagQueryAdapter implements TagQueryPort {
    private final TagRepository tagRepository;

    @Override
    public List<Tag> findByNameIn(List<String> tagNames) {
        return tagRepository.findByNameIn(tagNames);
    }
}
