package com.benchpress200.photique.user.domain.entity;

import com.benchpress200.photique.user.domain.entity.id.FollowId;
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
@NoArgsConstructor
@Getter
@Table(name = "follows")
@EntityListeners(AuditingEntityListener.class)
public class Follow {
    @EmbeddedId
    private FollowId id;

    @MapsId("followerId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @MapsId("followeeId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followee_id", nullable = false)
    private User followee;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Follow(
            User follower,
            User followee
    ) {
        this.follower = follower;
        this.followee = followee;

        this.id = new FollowId(
                follower.getId(),
                followee.getId()
        );
    }

    public static Follow of(
            User follower,
            User followee
    ) {
        return new Follow(follower, followee);
    }
}
