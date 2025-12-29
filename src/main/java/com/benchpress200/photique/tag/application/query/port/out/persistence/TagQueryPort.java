package com.benchpress200.photique.tag.application.query.port.out.persistence;

import com.benchpress200.photique.tag.domain.entity.Tag;
import java.util.List;

public interface TagQueryPort {
    List<Tag> findByNameIn(List<String> tagNames);
}
