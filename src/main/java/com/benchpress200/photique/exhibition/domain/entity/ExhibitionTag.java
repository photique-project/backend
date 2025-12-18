package com.benchpress200.photique.exhibition.domain.entity;

import com.benchpress200.photique.tag.domain.entity.Tag;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "exhibition_tags")
public class ExhibitionTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibition_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Exhibition exhibition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Tag tag;

    @Builder
    public ExhibitionTag(
            Exhibition exhibition,
            Tag tag
    ) {
        this.exhibition = exhibition;
        this.tag = tag;
    }
}
