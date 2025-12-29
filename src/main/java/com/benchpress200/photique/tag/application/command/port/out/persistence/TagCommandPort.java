package com.benchpress200.photique.tag.application.command.port.out.persistence;

import com.benchpress200.photique.tag.domain.entity.Tag;
import java.util.List;

public interface TagCommandPort {
    List<Tag> saveAll(List<Tag> tags);
}
