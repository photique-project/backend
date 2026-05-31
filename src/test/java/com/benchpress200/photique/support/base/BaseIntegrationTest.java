package com.benchpress200.photique.support.base;

import com.benchpress200.photique.config.TestDatabaseCleanerConfig;
import com.benchpress200.photique.constant.Profile;
import com.benchpress200.photique.support.util.DatabaseCleaner;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(Profile.TEST)
@Import(TestDatabaseCleanerConfig.class)
public abstract class BaseIntegrationTest extends BaseTestContainerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected DatabaseCleaner databaseCleaner;
}
