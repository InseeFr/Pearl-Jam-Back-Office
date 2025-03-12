package fr.insee.pearljam.integration;

import fr.insee.pearljam.api.utils.ScriptConstants;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@ActiveProfiles(profiles = {"demo"})
@ContextConfiguration
@SpringBootTest
@Transactional
class DemoDataIT {
    @Test
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void contextLoads() {
        // verifying demo data is loaded
    }
}
