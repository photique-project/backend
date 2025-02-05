package com.benchpress200.photique.singlework.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSingleWorkLike is a Querydsl query type for SingleWorkLike
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSingleWorkLike extends EntityPathBase<SingleWorkLike> {

    private static final long serialVersionUID = -1718508407L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSingleWorkLike singleWorkLike = new QSingleWorkLike("singleWorkLike");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QSingleWork singleWork;

    public final com.benchpress200.photique.user.domain.entity.QUser user;

    public QSingleWorkLike(String variable) {
        this(SingleWorkLike.class, forVariable(variable), INITS);
    }

    public QSingleWorkLike(Path<? extends SingleWorkLike> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSingleWorkLike(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSingleWorkLike(PathMetadata metadata, PathInits inits) {
        this(SingleWorkLike.class, metadata, inits);
    }

    public QSingleWorkLike(Class<? extends SingleWorkLike> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.singleWork = inits.isInitialized("singleWork") ? new QSingleWork(forProperty("singleWork"), inits.get("singleWork")) : null;
        this.user = inits.isInitialized("user") ? new com.benchpress200.photique.user.domain.entity.QUser(forProperty("user")) : null;
    }

}

