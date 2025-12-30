package com.benchpress200.photique.singlework.application.query.port.out.persistence;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;
import java.util.List;

public interface SingleWorkTagQueryPort {
    List<SingleWorkTag> findBySingleWorkWithTag(SingleWork singlework);
}
