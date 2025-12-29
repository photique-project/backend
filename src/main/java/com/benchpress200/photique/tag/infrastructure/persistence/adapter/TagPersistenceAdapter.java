package com.benchpress200.photique.tag.infrastructure.persistence.adapter;

import com.benchpress200.photique.tag.application.command.port.out.persistence.TagCommandPort;
import com.benchpress200.photique.tag.application.query.port.out.persistence.TagQueryPort;
import com.benchpress200.photique.tag.domain.entity.Tag;
import com.benchpress200.photique.tag.infrastructure.persistence.jpa.TagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TagPersistenceAdapter implements
        TagCommandPort,
        TagQueryPort {
    private final TagRepository tagRepository;

    @Override
    public List<Tag> saveAll(List<Tag> tags) {
        return tagRepository.saveAll(tags);
    }

    @Override
    public List<Tag> findByNameIn(List<String> tagNames) {
        return tagRepository.findByNameIn(tagNames);
    }
}
