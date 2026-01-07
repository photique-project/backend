package com.benchpress200.photique.notification.domain.entity;

import com.benchpress200.photique.notification.domain.enumeration.NotificationType;
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
@Table(name = "notifications")
@EntityListeners(AuditingEntityListener.class)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    @Builder
    public Notification(
            User receiver,
            NotificationType type,
            Long targetId
    ) {
        this.receiver = receiver;
        this.type = type;
        this.targetId = targetId;
    }

    public void read() {
        isRead = true;
    }

    public static Notification of(
            User receiver,
            NotificationType type,
            Long targetId
    ) {
        return Notification.builder()
                .receiver(receiver)
                .type(type)
                .targetId(targetId)
                .build();
    }

    public void delete() {
        deletedAt = LocalDateTime.now();
    }
}
