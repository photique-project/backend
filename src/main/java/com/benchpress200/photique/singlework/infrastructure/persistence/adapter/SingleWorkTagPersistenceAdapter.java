package com.benchpress200.photique.singlework.infrastructure.persistence.adapter;

import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkTagCommandPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkTagQueryPort;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;
import com.benchpress200.photique.singlework.infrastructure.persistence.jpa.SingleWorkTagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SingleWorkTagPersistenceAdapter implements
        SingleWorkTagQueryPort,
        SingleWorkTagCommandPort {
    private final SingleWorkTagRepository singleWorkTagRepository;

    @Override
    public List<SingleWorkTag> findBySingleWorkWithTag(SingleWork singlework) {
        return singleWorkTagRepository.findBySingleWorkWithTag(singlework);
    }

    @Override
    public SingleWorkTag save(SingleWorkTag singleWorkTag) {
        return singleWorkTagRepository.save(singleWorkTag);
    }

    @Override
    public void deleteBySingleWork(SingleWork singleWork) {
        singleWorkTagRepository.deleteBySingleWork(singleWork);
    }
}
