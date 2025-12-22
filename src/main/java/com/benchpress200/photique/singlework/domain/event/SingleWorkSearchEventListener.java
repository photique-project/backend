package com.benchpress200.photique.singlework.domain.event;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkNotFoundException;
import com.benchpress200.photique.singlework.domain.repository.SingleWorkRepository;
import com.benchpress200.photique.singlework.domain.repository.SingleWorkSearchRepository;
import com.benchpress200.photique.singlework.domain.repository.SingleWorkTagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SingleWorkSearchEventListener {
    private final SingleWorkSearchRepository singleWorkSearchRepository;
    private final SingleWorkRepository singleWorkRepository;
    private final SingleWorkTagRepository singleWorkTagRepository;

    // TODO: 이후 메시지 큐 도입한다면 메시지 발행 & 실패 재시도 & 컨슈머 비동기 처리 고려
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSingleWorkSearchCreationEventIfCommit(SingleWorkSearchCreationCommitEvent event) {
        Long singleWorkId = event.getSingleWorkId();
        SingleWork singleWork = singleWorkRepository.findWithWriter(singleWorkId)
                .orElseThrow(() -> new SingleWorkNotFoundException(singleWorkId));

        // 태그 조회
        List<SingleWorkTag> singleWorkTags = singleWorkTagRepository.findWithTag(singleWork);
        List<String> singleWorkTagNames = singleWorkTags.stream()
                .map(singleWorkTag -> singleWorkTag.getTag().getName())
                .toList();

        // 단일작품 검색 엔티티 생성
        SingleWorkSearch singleWorkSearch = SingleWorkSearch.of(
                singleWork,
                singleWorkTagNames
        );

        singleWorkSearchRepository.save(singleWorkSearch);
    }
}
