package com.benchpress200.photique.config;

import com.benchpress200.photique.auth.infrastructure.persistence.redis.AuthMailCodeRepository;
import com.benchpress200.photique.exhibition.infrastructure.persistence.elasticsearch.ExhibitionSearchRepository;
import com.benchpress200.photique.exhibition.infrastructure.persistence.jpa.ExhibitionBookmarkRepository;
import com.benchpress200.photique.exhibition.infrastructure.persistence.jpa.ExhibitionCommentRepository;
import com.benchpress200.photique.exhibition.infrastructure.persistence.jpa.ExhibitionLikeRepository;
import com.benchpress200.photique.exhibition.infrastructure.persistence.jpa.ExhibitionRepository;
import com.benchpress200.photique.exhibition.infrastructure.persistence.jpa.ExhibitionTagRepository;
import com.benchpress200.photique.exhibition.infrastructure.persistence.jpa.ExhibitionWorkRepository;
import com.benchpress200.photique.notification.infrastructure.persistence.jpa.NotificationRepository;
import com.benchpress200.photique.outbox.infrastructure.persistence.jpa.OutboxEventRepository;
import com.benchpress200.photique.singlework.infrastructure.persistence.elasticsearch.SingleWorkSearchRepository;
import com.benchpress200.photique.singlework.infrastructure.persistence.jpa.SingleWorkCommentRepository;
import com.benchpress200.photique.singlework.infrastructure.persistence.jpa.SingleWorkLikeRepository;
import com.benchpress200.photique.singlework.infrastructure.persistence.jpa.SingleWorkRepository;
import com.benchpress200.photique.singlework.infrastructure.persistence.jpa.SingleWorkTagRepository;
import com.benchpress200.photique.support.util.DatabaseCleaner;
import com.benchpress200.photique.tag.infrastructure.persistence.jpa.TagRepository;
import com.benchpress200.photique.user.infrastructure.persistence.jpa.FollowRepository;
import com.benchpress200.photique.user.infrastructure.persistence.jpa.UserRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestDatabaseCleanerConfig {
    @Bean
    public DatabaseCleaner databaseCleaner(
            FollowRepository followRepository,
            SingleWorkTagRepository singleWorkTagRepository,
            SingleWorkLikeRepository singleWorkLikeRepository,
            SingleWorkCommentRepository singleWorkCommentRepository,
            ExhibitionWorkRepository exhibitionWorkRepository,
            ExhibitionTagRepository exhibitionTagRepository,
            ExhibitionLikeRepository exhibitionLikeRepository,
            ExhibitionBookmarkRepository exhibitionBookmarkRepository,
            ExhibitionCommentRepository exhibitionCommentRepository,
            NotificationRepository notificationRepository,
            OutboxEventRepository outboxEventRepository,
            SingleWorkRepository singleWorkRepository,
            ExhibitionRepository exhibitionRepository,
            TagRepository tagRepository,
            UserRepository userRepository,
            AuthMailCodeRepository authMailCodeRepository,
            ExhibitionSearchRepository exhibitionSearchRepository,
            SingleWorkSearchRepository singleWorkSearchRepository
    ) {
        return new DatabaseCleaner(
                followRepository,
                singleWorkTagRepository,
                singleWorkLikeRepository,
                singleWorkCommentRepository,
                exhibitionWorkRepository,
                exhibitionTagRepository,
                exhibitionLikeRepository,
                exhibitionBookmarkRepository,
                exhibitionCommentRepository,
                notificationRepository,
                outboxEventRepository,
                singleWorkRepository,
                exhibitionRepository,
                tagRepository,
                userRepository,
                authMailCodeRepository,
                exhibitionSearchRepository,
                singleWorkSearchRepository
        );
    }
}
