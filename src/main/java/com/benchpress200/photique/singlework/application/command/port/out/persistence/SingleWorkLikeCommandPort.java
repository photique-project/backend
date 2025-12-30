package com.benchpress200.photique.singlework.application.command.port.out.persistence;

import com.benchpress200.photique.singlework.domain.entity.SingleWorkLike;

public interface SingleWorkLikeCommandPort {
    SingleWorkLike save(SingleWorkLike singleWorkLike);
}
