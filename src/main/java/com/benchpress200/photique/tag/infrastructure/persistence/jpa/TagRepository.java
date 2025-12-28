package com.benchpress200.photique.tag.infrastructure.persistence.jpa;

import com.benchpress200.photique.tag.domain.entity.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByNameIn(List<String> names);

    boolean existsByName(String name);

    Tag findByName(String name);
}
