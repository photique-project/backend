package com.benchpress200.photique.exhibition.domain.entity;

import com.benchpress200.photique.user.domain.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "exhibitions")
public class Exhibition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User writer;

    @Column(length = 30, nullable = false)
    private String title;

    @Column(length = 200, nullable = false)
    private String description;

    @Column(name = "card_color", length = 30, nullable = false)
    private String cardColor;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "exhibition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExhibitionWork> exhibitionWorks;

    @PrePersist
    public void prePersist() {
        viewCount = 0L;
        createdAt = LocalDateTime.now();
    }

    @Builder
    public Exhibition(
            User writer,
            String title,
            String description,
            String cardColor
    ) {
        this.writer = writer;
        this.title = title;
        this.description = description;
        this.cardColor = cardColor;
    }

    public void incrementView() {
        viewCount++;
    }

    public void markAsUpdated() {
        updatedAt = LocalDateTime.now();
    }
}
