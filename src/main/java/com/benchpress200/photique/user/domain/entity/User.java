package com.benchpress200.photique.user.domain.entity;

import com.benchpress200.photique.user.domain.enumeration.Role;
import com.benchpress200.photique.user.domain.enumeration.Source;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_email", columnList = "email"),
                @Index(name = "idx_nickname", columnList = "nickname")
        }
)
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(name = "profile_image", length = 2048)
    private String profileImage;

    @Column(length = 50)
    private String introduction;

    @Enumerated(EnumType.STRING)
    private Source source;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.role = Role.USER;
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public User(
            final String email,
            final String password,
            final String nickname,
            final String introduction,
            final String profileImage,
            final Source source
    ) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.introduction = introduction;
        this.profileImage = profileImage;
        this.source = source;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void markAsUpdated() {
        updatedAt = LocalDateTime.now();
    }
}
