package com.benchpress200.photique.exhibition.infrastructure.persistence.jpa;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionTag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExhibitionTagRepository extends JpaRepository<ExhibitionTag, Long> {
    @Query("""
            SELECT et
            FROM ExhibitionTag et
            JOIN FETCH et.tag
            WHERE et.exhibition = :exhibition
            """)
    List<ExhibitionTag> findByExhibitionWithTag(@Param("exhibition") Exhibition exhibition);
}
