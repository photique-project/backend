package com.benchpress200.photique.singlework.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSingleWork is a Querydsl query type for SingleWork
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSingleWork extends EntityPathBase<SingleWork> {

    private static final long serialVersionUID = 534952402L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSingleWork singleWork = new QSingleWork("singleWork");

    public final EnumPath<com.benchpress200.photique.singlework.domain.enumeration.Aperture> aperture = createEnum("aperture", com.benchpress200.photique.singlework.domain.enumeration.Aperture.class);

    public final StringPath camera = createString("camera");

    public final EnumPath<com.benchpress200.photique.singlework.domain.enumeration.Category> category = createEnum("category", com.benchpress200.photique.singlework.domain.enumeration.Category.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath image = createString("image");

    public final EnumPath<com.benchpress200.photique.singlework.domain.enumeration.ISO> iso = createEnum("iso", com.benchpress200.photique.singlework.domain.enumeration.ISO.class);

    public final StringPath lens = createString("lens");

    public final NumberPath<Long> likeCount = createNumber("likeCount", Long.class);

    public final StringPath location = createString("location");

    public final EnumPath<com.benchpress200.photique.singlework.domain.enumeration.ShutterSpeed> shutterSpeed = createEnum("shutterSpeed", com.benchpress200.photique.singlework.domain.enumeration.ShutterSpeed.class);

    public final StringPath title = createString("title");

    public final NumberPath<Long> viewCount = createNumber("viewCount", Long.class);

    public final com.benchpress200.photique.user.domain.entity.QUser writer;

    public QSingleWork(String variable) {
        this(SingleWork.class, forVariable(variable), INITS);
    }

    public QSingleWork(Path<? extends SingleWork> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSingleWork(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSingleWork(PathMetadata metadata, PathInits inits) {
        this(SingleWork.class, metadata, inits);
    }

    public QSingleWork(Class<? extends SingleWork> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.writer = inits.isInitialized("writer") ? new com.benchpress200.photique.user.domain.entity.QUser(forProperty("writer")) : null;
    }

}

