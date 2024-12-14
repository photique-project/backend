package com.benchpress200.photique.user.domain.entity;

import com.benchpress200.photique.user.domain.enumeration.AuthType;
import com.benchpress200.photique.user.domain.enumeration.Role;
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
import org.hibernate.annotations.ColumnDefault;
import org.springframework.context.event.EventListener;

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

    @Column(nullable = false)
    private Long coin;

    @Column(name= "profile_image" , length = 2048)
    private String profileImage;

    @Enumerated(EnumType.STRING)
    private AuthType authType;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name= "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        coin = 0L;
        role = Role.USER;
        createdAt = LocalDateTime.now();
    }

    @Builder
    public User(
            String email,
            String password,
            String nickname,
            String profileImage,
            AuthType authType
    ) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.authType = authType;
    }
}
