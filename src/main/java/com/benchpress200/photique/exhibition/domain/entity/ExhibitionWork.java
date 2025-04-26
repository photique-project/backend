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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Exhibition exhibition;

    @Column(length = 2048, nullable = false)
    private String image;

    @Column(length = 30, nullable = false)
    private String title;

    @Column(length = 200, nullable = false)
    private String description;


    @Builder
    public ExhibitionWork(
            final Exhibition exhibition,
            final String image,
            final String title,
            final String description
    ) {
        this.exhibition = exhibition;
        this.image = image;
        this.title = title;
        this.description = description;
    }
}
