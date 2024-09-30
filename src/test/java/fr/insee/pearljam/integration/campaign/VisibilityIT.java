package fr.insee.pearljam.integration.campaign;

import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import fr.insee.pearljam.api.utils.ScriptConstants;
import fr.insee.pearljam.infrastructure.campaign.entity.VisibilityDB;
import fr.insee.pearljam.infrastructure.campaign.entity.VisibilityDBId;
import fr.insee.pearljam.infrastructure.campaign.jpa.VisibilityJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@ActiveProfiles(profiles = {"auth", "test"})
@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class VisibilityIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VisibilityJpaRepository visibilityRepository;

    @Test
    void testGetVisibilities() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                get("/api/campaign/SIMPSONS2020X00/visibilities")
                        .with(authentication(AuthenticatedUserTestHelper.AUTH_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        String resultContent = mvcResult.getResponse().getContentAsString();
        String expectedResult = """
                [
                   {
                      "organizationalUnit":"OU-NORTH",
                      "managementStartDate":1718966154301,
                      "interviewerStartDate":1719052554302,
                      "identificationPhaseStartDate":1719138954303,
                      "collectionStartDate":1719225354304,
                      "collectionEndDate":1721903754305,
                      "endDate":1724582154306
                   },
                   {
                      "organizationalUnit":"OU-SOUTH",
                      "managementStartDate":1718966154308,
                      "interviewerStartDate":1719052554309,
                      "identificationPhaseStartDate":1719138954310,
                      "collectionStartDate":1719225354314,
                      "collectionEndDate":1721903754315,
                      "endDate":1724582154316
                   }
                ]
                """;
        JSONAssert.assertEquals(resultContent, expectedResult, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    @DisplayName("Should update visibility")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void testUpdateVisibility() throws Exception {
        String organizationalUnitId = "OU-NORTH";
        String campaignId = "SIMPSONS2020X00";
        String updatePath = String.format("/api/campaign/%s/organizational-unit/%s/visibility", campaignId, organizationalUnitId);
        String content = """
        {
            "managementStartDate":1718966200000,
            "interviewerStartDate":null,
            "identificationPhaseStartDate":1719138954308,
            "collectionStartDate":null,
            "collectionEndDate":1921903754308,
            "endDate":2024582154308
        }""";

        mockMvc.perform(put(updatePath)
                        .with(authentication(AuthenticatedUserTestHelper.AUTH_LOCAL_USER))
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        VisibilityDB visibilityDBToCheck = visibilityRepository.getReferenceById(new VisibilityDBId(organizationalUnitId, campaignId));

        assertThat(visibilityDBToCheck.getCampaign().getId()).isEqualTo(campaignId);
        assertThat(visibilityDBToCheck.getOrganizationUnit().getId()).isEqualTo(organizationalUnitId);
        assertThat(visibilityDBToCheck.getManagementStartDate()).isEqualTo(1718966200000L);
        assertThat(visibilityDBToCheck.getInterviewerStartDate()).isEqualTo(1719052554302L);
        assertThat(visibilityDBToCheck.getIdentificationPhaseStartDate()).isEqualTo(1719138954308L);
        assertThat(visibilityDBToCheck.getCollectionStartDate()).isEqualTo(1719225354304L);
        assertThat(visibilityDBToCheck.getCollectionEndDate()).isEqualTo(1921903754308L);
        assertThat(visibilityDBToCheck.getEndDate()).isEqualTo(2024582154308L);
    }
}
