package com.benchpress200.photique.singlework.domain.entity;

import com.benchpress200.photique.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "singlework_comments")
public class SingleWorkComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "singlework_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SingleWork singleWork;

    @Column(length = 300, nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public SingleWorkComment(
            User writer,
            SingleWork singleWork,
            String content
    ) {
        this.writer = writer;
        this.singleWork = singleWork;
        this.content = content;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
