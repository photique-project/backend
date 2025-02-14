package com.benchpress200.photique.exhibition.application;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionCommentCreateRequest;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import com.benchpress200.photique.exhibition.exception.ExhibitionException;
import com.benchpress200.photique.exhibition.infrastructure.ExhibitionCommentRepository;
import com.benchpress200.photique.exhibition.infrastructure.ExhibitionRepository;
import com.benchpress200.photique.singlework.exception.SingleWorkException;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.infrastructure.UserRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ExhibitionCommentServiceImpl implements ExhibitionCommentService {

    private final UserRepository userRepository;
    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionCommentRepository exhibitionCommentRepository;
    private final ElasticsearchClient elasticsearchClient;

    @Override
    public void createExhibitionComment(final ExhibitionCommentCreateRequest exhibitionCommentCreateRequest) {
        // elastic search 데이터 업데이트를 위한 컬렉션
        Map<String, Object> updateFields = new HashMap<>();

        // 작성자 조회
        Long writerId = exhibitionCommentCreateRequest.getWriterId();
        User writer = userRepository.findById(writerId).orElseThrow(
                () -> new ExhibitionException("User with ID " + writerId + " is not found.", HttpStatus.NOT_FOUND)
        );

        // 전시회 조회
        Long exhibitionId = exhibitionCommentCreateRequest.getExhibitionId();
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId).orElseThrow(
                () -> new ExhibitionException("Exhibition with ID " + writerId + " is not found.", HttpStatus.NOT_FOUND)
        );

        // 저장
        ExhibitionComment exhibitionComment = exhibitionCommentCreateRequest.toEntity(exhibition, writer);
        exhibitionCommentRepository.save(exhibitionComment);

        // elastic search 데이터 업데이트
        updateFields.put("commentCount", exhibitionCommentRepository.countByExhibitionId(exhibitionId));

        UpdateRequest<Map<String, Object>, ?> updateRequest = UpdateRequest.of(u -> u
                .index("singleworks")
                .id(exhibition.getId().toString())
                .doc(updateFields)
        );

        try {
            elasticsearchClient.update(updateRequest, Map.class);
        } catch (IOException e) {
            throw new SingleWorkException("Elastic search network error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
