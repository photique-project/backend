package com.benchpress200.photique.singlework.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSingleWorkComment is a Querydsl query type for SingleWorkComment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSingleWorkComment extends EntityPathBase<SingleWorkComment> {

    private static final long serialVersionUID = 702577709L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSingleWorkComment singleWorkComment = new QSingleWorkComment("singleWorkComment");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> likeCount = createNumber("likeCount", Long.class);

    public final QSingleWork singleWork;

    public final com.benchpress200.photique.user.domain.entity.QUser writer;

    public QSingleWorkComment(String variable) {
        this(SingleWorkComment.class, forVariable(variable), INITS);
    }

    public QSingleWorkComment(Path<? extends SingleWorkComment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSingleWorkComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSingleWorkComment(PathMetadata metadata, PathInits inits) {
        this(SingleWorkComment.class, metadata, inits);
    }

    public QSingleWorkComment(Class<? extends SingleWorkComment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.singleWork = inits.isInitialized("singleWork") ? new QSingleWork(forProperty("singleWork"), inits.get("singleWork")) : null;
        this.writer = inits.isInitialized("writer") ? new com.benchpress200.photique.user.domain.entity.QUser(forProperty("writer")) : null;
    }

}

