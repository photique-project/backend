package com.benchpress200.photique.singlework.application.command.port.out.persistence;


import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;

public interface SingleWorkTagCommandPort {
    SingleWorkTag save(SingleWorkTag singleWorkTag);

    void deleteBySingleWork(SingleWork singleWork);
}
