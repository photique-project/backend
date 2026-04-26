package com.benchpress200.photique.singlework.application.command.port.out.persistence;


import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;
import java.util.List;

public interface SingleWorkTagCommandPort {
    SingleWorkTag save(SingleWorkTag singleWorkTag);

    List<SingleWorkTag> saveAll(List<SingleWorkTag> singleWorkTags);

    void deleteBySingleWork(SingleWork singleWork);

    void deleteAll();
}
