package com.benchpress200.photique.singlework;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;

import com.benchpress200.photique.image.domain.ImageDomainService;
import com.benchpress200.photique.singlework.application.SingleWorkService;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkCreateRequest;
import com.benchpress200.photique.singlework.domain.repository.SingleWorkRepository;
import com.benchpress200.photique.singlework.domain.repository.SingleWorkSearchRepository;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
@DisplayName("단일 작품 서비스 로직 테스트")
public class SingleWorkServiceTest {
    @Autowired
    private SingleWorkRepository singleWorkRepository;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SingleWorkService singleWorkService;

    // MockBean은 기존에 사용되던 빈의 껍데기만 가져오고 내부의 구현 부분은 모두 사용자에게 위임
    // 필요에 따라 조작 가능
    @MockitoSpyBean
    private SingleWorkSearchRepository singleWorkSearchRepository;

    @MockitoSpyBean
    private ImageDomainService imageDomainService;

    // 메서드 실행 전 1번 유저 생성
    @BeforeEach
    void joinTestUser() {
        userRepository.save(
                User.builder()
                        .email("abc@gmail.com")
                        .password("pasword12!@")
                        .nickname("ian")
                        .profileImage("profile.jpg")
                        .build()
        );
    }

    // 클린업
    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
        singleWorkRepository.deleteAll();
        singleWorkSearchRepository.deleteAll();
    }

    @Test
    @DisplayName("단일 작품 생성 테스트")
    void testSingleWorkCreate() {
        // ES 저장 로직은 AOP로 동작하는데 ES에서 저장 예외 발생했을 때
        // MySQL에서 롤백 정상 진행되는지 확인
        // 하고 전시회와 유저 데이터도 동일하게 적용
        // 이미지처리도 S3가 아닌 목업으로 처리하도록 해보자
        Long userId = 1L;
        SingleWorkCreateRequest request = new SingleWorkCreateRequest();
        request.setWriterId(userId);
        request.setCamera("camera");
        request.setLens("lens");
        request.setAperture("미입력");
        request.setShutterSpeed("미입력");
        request.setIso("미입력");
        request.setLocation("location");
        request.setCategory("landscape");
        request.setDate(LocalDate.of(2025, 8, 1));
        request.setTags(new ArrayList<>());
        request.setTitle("title");
        request.setDescription("description");

        // 이미지 업로드 성공 모킹
        willReturn("url")
                .given(imageDomainService)
                .upload(any(), any());

        // ES 저장 실패 모킹
        willThrow(new RuntimeException("ES 저장 실패"))
                .given(singleWorkSearchRepository)
                .saveAll(any());

        // when
        assertThrows(RuntimeException.class, () -> singleWorkService.postNewSingleWork(request));

        // then (MySQL 롤백 확인)
        assertThat(singleWorkRepository.findAll()).isEmpty();
    }
}
