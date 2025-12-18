package com.benchpress200.photique.user.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor
@Table(
        name = "follows",
        uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followee_id"})
)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Follow {
    // [follower, followee] 쌍이 유일해서, 따로 id를 두지 않고 복합 PK로 둘 수 있지만,
    // 거의 동시에 생성되는 레코드는 createdAt으로 삽입 순서를 구분할 수 없고
    // JPA 기반의 조회를 할 때 id 편의성 떄문에 별도 id 칼럼 유지
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followee_id", nullable = false)
    private User followee;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Follow(
            User follower,
            User followee
    ) {
        this.follower = follower;
        this.followee = followee;
    }

    public static Follow of(
            User follower,
            User followee
    ) {
        return Follow.builder()
                .follower(follower)
                .followee(followee)
                .build();
    }
}
