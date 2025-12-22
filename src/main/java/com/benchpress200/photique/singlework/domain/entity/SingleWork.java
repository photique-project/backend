package com.benchpress200.photique.singlework.domain.entity;

import com.benchpress200.photique.singlework.domain.enumeration.Aperture;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.ISO;
import com.benchpress200.photique.singlework.domain.enumeration.ShutterSpeed;
import com.benchpress200.photique.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "singleworks")
@EntityListeners(AuditingEntityListener.class)
public class SingleWork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false)
    private String title;

    @Column(length = 500, nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
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

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public SingleWork(
            User writer,
            String image,
            String camera,
            String lens,
            Aperture aperture,
            ShutterSpeed shutterSpeed,
            ISO iso,
            String location,
            Category category,
            LocalDate date,
            String title,
            String description
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
        this.viewCount = 0L;
    }

    public boolean isOwnedBy(Long writerId) {
        return writer.getId().equals(writerId);
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateCamera(String camera) {
        this.camera = camera;
    }

    public void updateLens(String lens) {
        this.lens = lens;
    }

    public void updateAperture(Aperture aperture) {
        this.aperture = aperture;
    }

    public void updateShutterSpeed(ShutterSpeed shutterSpeed) {
        this.shutterSpeed = shutterSpeed;
    }

    public void updateIso(ISO iso) {
        this.iso = iso;
    }

    public void updateLocation(String location) {
        this.location = location;
    }

    public void updateCategory(Category category) {
        this.category = category;
    }

    public void updateDate(LocalDate date) {
        this.date = date;
    }
}
