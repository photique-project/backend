package com.benchpress200.photique.exhibition.domain.entity;

import com.benchpress200.photique.exhibition.domain.entity.id.ExhibitionLikeId;
import com.benchpress200.photique.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "exhibition_likes")
public class ExhibitionLike {
    @EmbeddedId
    private ExhibitionLikeId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("exhibitionId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibition_id", nullable = false)
    private Exhibition exhibition;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private ExhibitionLike(
            User user,
            Exhibition exhibition
    ) {
        this.user = user;
        this.exhibition = exhibition;

        this.id = new ExhibitionLikeId(
                user.getId(),
                exhibition.getId()
        );
    }

    public static ExhibitionLike of(
            User user,
            Exhibition exhibition
    ) {
        return new ExhibitionLike(user, exhibition);
    }
}
