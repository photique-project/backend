package com.benchpress200.photique.common.transaction;

import com.benchpress200.photique.common.transaction.rollbackcontext.ElasticsearchUserRollbackContext;
import com.benchpress200.photique.common.transaction.rollbackcontext.ImageRollbackContext;
import com.benchpress200.photique.image.infrastructure.ImageUploader;
import com.benchpress200.photique.user.domain.entity.UserSearch;
import com.benchpress200.photique.user.infrastructure.UserSearchRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class TransactionRollbackListener {
    // 롤백리스너에서는 인프라 롤백을 위해 인프라 바로 호출
    private final ImageUploader imageUploader;
    private final UserSearchRepository userSearchRepository;

    // 트랜잭션 실패 시 S3에 업로드된 이미지 삭제
    // 트랜잭션 어노테이션이 적용된 곳에서 예외 발생 시 실행
    @AfterThrowing(pointcut = "@annotation(jakarta.transaction.Transactional)")
    public void handleTransactionRollback() {
        // 롤백 컨텍스트 가져오기
        List<String> uploadedImages = ImageRollbackContext.getUploadedImages();

        // 롤백해야될 s3이미지 삭제
        if (!uploadedImages.isEmpty()) {
            uploadedImages.forEach(imageUploader::delete);
        }

        // 롤백 데이터 클리어
        ImageRollbackContext.clear();
        ElasticsearchUserRollbackContext.clear();
    }

    // 트랜잭션 정상 완료되면 해당 스레드 컨텍스트 클리어
    @AfterReturning(pointcut = "@annotation(jakarta.transaction.Transactional)")
    public void handleTransactionCommit() {
        ImageRollbackContext.clear(); // 이미지 클리어

        // 엘라스틱서치 쿼리 실행
        List<UserSearch> documentsToSave = ElasticsearchUserRollbackContext.getDocumentsToSave();
        if (!documentsToSave.isEmpty()) {
            userSearchRepository.saveAll(documentsToSave);
        }

        List<UserSearch> documentsToUpdate = ElasticsearchUserRollbackContext.getDocumentsToUpdate();
        if (!documentsToUpdate.isEmpty()) {
            documentsToUpdate.forEach(userSearchRepository::update);
        }

        List<UserSearch> documentsToDelete = ElasticsearchUserRollbackContext.getDocumentsToDelete();
        if (!documentsToDelete.isEmpty()) {
            userSearchRepository.deleteAll(documentsToDelete);
        }

        ElasticsearchUserRollbackContext.clear(); // 엘라스틱서치 컨텍스트 클리어
    }
}
