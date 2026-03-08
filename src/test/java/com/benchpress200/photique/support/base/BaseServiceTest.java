package com.benchpress200.photique.support.base;

import com.benchpress200.photique.constant.Profile;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(Profile.TEST)
public abstract class BaseServiceTest {
}
