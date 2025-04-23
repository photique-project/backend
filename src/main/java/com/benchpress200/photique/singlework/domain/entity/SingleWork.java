package com.benchpress200.photique.singlework.domain.entity;

import com.benchpress200.photique.singlework.domain.enumeration.Aperture;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.ISO;
import com.benchpress200.photique.singlework.domain.enumeration.ShutterSpeed;
import com.benchpress200.photique.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "singleworks")
public class SingleWork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User writer;

    @Column(length = 2048, nullable = false)
    private String image;

    @Column(length = 50, nullable = false)
    private String camera;

    @Column(length = 50)
    private String lens;

    @Enumerated(EnumType.STRING)
    private Aperture aperture;

    @Enumerated(EnumType.STRING)
    @Column(name = "shuter_speed")
    private ShutterSpeed shutterSpeed;

    @Enumerated(EnumType.STRING)
    private ISO iso;

    @Column(length = 50)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 30, nullable = false)
    private String title;

    @Column(length = 500, nullable = false)
    private String description;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        viewCount = 0L;
        createdAt = LocalDateTime.now();
    }

    @Builder
    public SingleWork(
            final User writer,
            final String image,
            final String camera,
            final String lens,
            final Aperture aperture,
            final ShutterSpeed shutterSpeed,
            final ISO iso,
            final String location,
            final Category category,
            final LocalDate date,
            final String title,
            final String description
    ) {
        this.writer = writer;
        this.image = image;
        this.camera = camera;
        this.lens = lens;
        this.aperture = aperture;
        this.shutterSpeed = shutterSpeed;
        this.iso = iso;
        this.location = location;
        this.category = category;
        this.date = date;
        this.title = title;
        this.description = description;
    }

    public void updateImage(final String image) {
        this.image = image;
    }

    public void updateCamera(final String camera) {
        this.camera = camera;
    }

    public void updateLens(final String lens) {
        this.lens = lens;
    }

    public void updateAperture(final Aperture aperture) {
        this.aperture = aperture;
    }

    public void updateShutterSpeed(final ShutterSpeed shutterSpeed) {
        this.shutterSpeed = shutterSpeed;
    }

    public void updateIso(final ISO iso) {
        this.iso = iso;
    }

    public void updateLocation(final String location) {
        this.location = location;
    }

    public void updateCategory(final Category category) {
        this.category = category;
    }

    public void updateDate(final LocalDate date) {
        this.date = date;
    }

    public void updateTitle(final String title) {
        this.title = title;
    }

    public void updateDescription(final String description) {
        this.description = description;
    }

    public void incrementView() {
        viewCount++;
    }

    public void markAsUpdated() {
        updatedAt = LocalDateTime.now();
    }
}
