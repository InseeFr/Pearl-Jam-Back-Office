package fr.insee.pearljam.integration.campaign;

import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import fr.insee.pearljam.api.utils.ScriptConstants;
import fr.insee.pearljam.infrastructure.campaign.entity.CommunicationInformationDB;
import fr.insee.pearljam.infrastructure.campaign.entity.CommunicationInformationDBId;
import fr.insee.pearljam.infrastructure.campaign.jpa.CommunicationInformationJpaRepository;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("auth")
@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class CommunicationInformationIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommunicationInformationJpaRepository communicationInformationRepository;

    @Test
    void testGetCommunicationInformations() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get("/api/campaign/SIMPSONS2020X00/communication-informations")
                                .with(authentication(AuthenticatedUserTestHelper.AUTH_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        String resultContent = mvcResult.getResponse().getContentAsString();
        String expectedResult = """
                [
                  {
                     "organizationalUnit": "OU-NORTH",
                     "mail": "north-simpsons@nooneknows.fr",
                     "tel": "0321234567"
                  }
               ]""";
        JSONAssert.assertEquals(resultContent, expectedResult, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    @DisplayName("Should update communication information")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void testUpdateCommunicationInformation() throws Exception {
        String organizationalUnitId = "OU-NORTH";
        String campaignId = "SIMPSONS2020X00";
        String updatePath = String.format("/api/campaign/%s/organizational-unit/%s/communication-information", campaignId, organizationalUnitId);
        String content = """
        {
            "mail": "newMail",
            "tel": "newTel"
        }""";

        mockMvc.perform(put(updatePath)
                        .with(authentication(AuthenticatedUserTestHelper.AUTH_ADMIN))
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        CommunicationInformationDB communicationDBToCheck = communicationInformationRepository.getReferenceById(new CommunicationInformationDBId(organizationalUnitId, campaignId));

        assertThat(communicationDBToCheck.getCampaign().getId()).isEqualTo(campaignId);
        assertThat(communicationDBToCheck.getOrganizationUnit().getId()).isEqualTo(organizationalUnitId);
        assertThat(communicationDBToCheck.getMail()).isEqualTo("newMail");
        assertThat(communicationDBToCheck.getTel()).isEqualTo("newTel");
    }
}
