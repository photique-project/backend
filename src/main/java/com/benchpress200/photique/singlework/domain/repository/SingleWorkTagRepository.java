package com.benchpress200.photique.singlework.domain.repository;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SingleWorkTagRepository extends JpaRepository<SingleWorkTag, Long> {
    List<SingleWorkTag> findBySingleWorkId(Long singleWorkId);

    @Query(
            "SELECT swt " +
                    "FROM SingleWorkTag swt " +
                    "JOIN FETCH swt.tag " +
                    "WHERE swt.singleWork = :singleWork"
    )
    List<SingleWorkTag> findWithTag(@Param("singleWork") SingleWork singleWork);

    List<SingleWorkTag> findBySingleWork(SingleWork singleWork);

    void deleteBySingleWorkId(Long singleWorkId);

    void deleteBySingleWork(SingleWork singleWork);
}
