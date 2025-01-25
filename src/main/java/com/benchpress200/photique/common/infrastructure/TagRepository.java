package com.benchpress200.photique.common.infrastructure;

import com.benchpress200.photique.common.domain.entity.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAllByNameIn(List<String> names);
}
