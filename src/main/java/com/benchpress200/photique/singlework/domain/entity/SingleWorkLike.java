package com.benchpress200.photique.singlework.domain.entity;

import com.benchpress200.photique.singlework.domain.entity.id.SingleWorkLikeId;
import com.benchpress200.photique.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "singlework_likes")
public class SingleWorkLike {
    @EmbeddedId
    private SingleWorkLikeId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("singleWorkId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "singlework_id", nullable = false)
    private SingleWork singleWork;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    private SingleWorkLike(
            User user,
            SingleWork singleWork
    ) {
        this.user = user;
        this.singleWork = singleWork;

        this.id = new SingleWorkLikeId(
                user.getId(),
                singleWork.getId()
        );
    }

    public static SingleWorkLike of(
            User user,
            SingleWork singleWork
    ) {
        return new SingleWorkLike(user, singleWork);
    }
}
