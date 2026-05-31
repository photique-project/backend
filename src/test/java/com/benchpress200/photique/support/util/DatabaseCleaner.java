package com.benchpress200.photique.support.util;

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
import com.benchpress200.photique.tag.infrastructure.persistence.jpa.TagRepository;
import com.benchpress200.photique.user.infrastructure.persistence.jpa.FollowRepository;
import com.benchpress200.photique.user.infrastructure.persistence.jpa.UserRepository;

public class DatabaseCleaner {
    private final FollowRepository followRepository;
    private final SingleWorkTagRepository singleWorkTagRepository;
    private final SingleWorkLikeRepository singleWorkLikeRepository;
    private final SingleWorkCommentRepository singleWorkCommentRepository;
    private final ExhibitionWorkRepository exhibitionWorkRepository;
    private final ExhibitionTagRepository exhibitionTagRepository;
    private final ExhibitionLikeRepository exhibitionLikeRepository;
    private final ExhibitionBookmarkRepository exhibitionBookmarkRepository;
    private final ExhibitionCommentRepository exhibitionCommentRepository;
    private final NotificationRepository notificationRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final SingleWorkRepository singleWorkRepository;
    private final ExhibitionRepository exhibitionRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final AuthMailCodeRepository authMailCodeRepository;
    private final ExhibitionSearchRepository exhibitionSearchRepository;
    private final SingleWorkSearchRepository singleWorkSearchRepository;

    public DatabaseCleaner(
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
        this.followRepository = followRepository;
        this.singleWorkTagRepository = singleWorkTagRepository;
        this.singleWorkLikeRepository = singleWorkLikeRepository;
        this.singleWorkCommentRepository = singleWorkCommentRepository;
        this.exhibitionWorkRepository = exhibitionWorkRepository;
        this.exhibitionTagRepository = exhibitionTagRepository;
        this.exhibitionLikeRepository = exhibitionLikeRepository;
        this.exhibitionBookmarkRepository = exhibitionBookmarkRepository;
        this.exhibitionCommentRepository = exhibitionCommentRepository;
        this.notificationRepository = notificationRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.singleWorkRepository = singleWorkRepository;
        this.exhibitionRepository = exhibitionRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.authMailCodeRepository = authMailCodeRepository;
        this.exhibitionSearchRepository = exhibitionSearchRepository;
        this.singleWorkSearchRepository = singleWorkSearchRepository;
    }

    public void clean() {
        followRepository.deleteAll();
        singleWorkTagRepository.deleteAll();
        singleWorkLikeRepository.deleteAll();
        singleWorkCommentRepository.deleteAll();
        exhibitionWorkRepository.deleteAll();
        exhibitionTagRepository.deleteAll();
        exhibitionLikeRepository.deleteAll();
        exhibitionBookmarkRepository.deleteAll();
        exhibitionCommentRepository.deleteAll();
        notificationRepository.deleteAll();
        outboxEventRepository.deleteAll();
        singleWorkRepository.deleteAll();
        exhibitionRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();
        authMailCodeRepository.deleteAll();
        exhibitionSearchRepository.deleteAll();
        singleWorkSearchRepository.deleteAll();
    }
}
