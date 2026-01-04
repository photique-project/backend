package com.benchpress200.photique.exhibition.domain.entity;

import jakarta.persistence.Column;
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

@Entity
@Getter
@NoArgsConstructor
@Table(name = "exhibition_works")
public class ExhibitionWork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibition_id", nullable = false)
    private Exhibition exhibition;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(length = 30, nullable = false)
    private String title;

    @Column(length = 200, nullable = false)
    private String description;

    @Column(length = 2048, nullable = false)
    private String image;


    @Builder
    public ExhibitionWork(
            Exhibition exhibition,
            Integer displayOrder,
            String title,
            String description,
            String image
    ) {
        this.exhibition = exhibition;
        this.displayOrder = displayOrder;
        this.title = title;
        this.description = description;
        this.image = image;
    }
}
