package com.benchpress200.photique.singlework.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSingleWorkTag is a Querydsl query type for SingleWorkTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSingleWorkTag extends EntityPathBase<SingleWorkTag> {

    private static final long serialVersionUID = -1856543640L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSingleWorkTag singleWorkTag = new QSingleWorkTag("singleWorkTag");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QSingleWork singleWork;

    public final com.benchpress200.photique.common.domain.entity.QTag tag;

    public QSingleWorkTag(String variable) {
        this(SingleWorkTag.class, forVariable(variable), INITS);
    }

    public QSingleWorkTag(Path<? extends SingleWorkTag> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSingleWorkTag(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSingleWorkTag(PathMetadata metadata, PathInits inits) {
        this(SingleWorkTag.class, metadata, inits);
    }

    public QSingleWorkTag(Class<? extends SingleWorkTag> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.singleWork = inits.isInitialized("singleWork") ? new QSingleWork(forProperty("singleWork"), inits.get("singleWork")) : null;
        this.tag = inits.isInitialized("tag") ? new com.benchpress200.photique.common.domain.entity.QTag(forProperty("tag")) : null;
    }

}

