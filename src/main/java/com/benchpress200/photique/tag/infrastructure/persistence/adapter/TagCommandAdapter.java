package com.benchpress200.photique.tag.infrastructure.persistence.adapter;

import com.benchpress200.photique.tag.domain.entity.Tag;
import com.benchpress200.photique.tag.domain.port.TagCommandPort;
import com.benchpress200.photique.tag.infrastructure.persistence.jpa.TagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TagCommandAdapter implements TagCommandPort {
    private final TagRepository tagRepository;

    @Override
    public List<Tag> saveAll(List<Tag> tags) {
        return tagRepository.saveAll(tags);
    }
}
