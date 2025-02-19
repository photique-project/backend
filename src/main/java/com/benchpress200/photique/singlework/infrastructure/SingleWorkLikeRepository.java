package com.benchpress200.photique.singlework.infrastructure;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkLike;
import com.benchpress200.photique.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SingleWorkLikeRepository extends JpaRepository<SingleWorkLike, Long> {


    void deleteByUser(User user);

    void deleteBySingleWork(SingleWork singleWork);

    Long countBySingleWork(SingleWork singleWork);

    void deleteByUserAndSingleWork(User user, SingleWork singleWork);

    boolean existsByUserAndSingleWork(User user, SingleWork singleWork);
}
