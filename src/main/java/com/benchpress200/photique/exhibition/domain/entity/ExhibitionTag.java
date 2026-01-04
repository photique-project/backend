package com.benchpress200.photique.exhibition.domain.entity;

import com.benchpress200.photique.exhibition.domain.entity.id.ExhibitionTagId;
import com.benchpress200.photique.tag.domain.entity.Tag;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "exhibition_tags")
public class ExhibitionTag {
    @EmbeddedId
    private ExhibitionTagId id;

    @MapsId("exhibitionId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibition_id", nullable = false)
    private Exhibition exhibition;

    @MapsId("tagId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    private ExhibitionTag(
            Exhibition exhibition,
            Tag tag
    ) {
        this.exhibition = exhibition;
        this.tag = tag;

        this.id = new ExhibitionTagId(
                exhibition.getId(),
                tag.getId()
        );
    }

    public static ExhibitionTag of(
            Exhibition exhibition,
            Tag tag
    ) {
        return new ExhibitionTag(exhibition, tag);
    }
}
