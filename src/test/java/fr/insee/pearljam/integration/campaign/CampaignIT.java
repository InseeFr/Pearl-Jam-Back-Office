package fr.insee.pearljam.integration.campaign;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.domain.*;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import fr.insee.pearljam.api.utils.ScriptConstants;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationMedium;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import fr.insee.pearljam.infrastructure.campaign.entity.CommunicationTemplateDB;
import fr.insee.pearljam.infrastructure.campaign.entity.VisibilityDB;
import org.json.JSONException;
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

import java.text.ParseException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("auth")
@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class CampaignIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CampaignRepository campaignRepository;

    /**
     * Test that the GET endpoint "api/campaigns"
     *
     * @throws InterruptedException
     * @throws JSONException
     * @throws ParseException
     */
    @Test
    @DisplayName("Should retrieve campaign")
    void testGetCampaign() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/campaign/SIMPSONS2020X00")
                        .with(authentication(AuthenticatedUserTestHelper.AUTH_LOCAL_USER))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentResult = mvcResult.getResponse().getContentAsString();
        String expectedResult = """
                {
                   "campaign":"SIMPSONS2020X00",
                   "campaignLabel":"Survey on the Simpsons tv show 2020",
                   "visibilities":[
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
                   ],
                   "referents":[
                      {
                         "firstName":"Gerard",
                         "lastName":"Menvuca",
                         "phoneNumber":"0303030303",
                         "role":"PRIMARY"
                      }
                   ],
                   "email":"first.email@test.com",
                   "identificationConfiguration":"IASCO",
                   "contactOutcomeConfiguration":"F2F",
                   "contactAttemptConfiguration":"F2F"
                }
                """;
        JSONAssert.assertEquals(contentResult, expectedResult, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    @DisplayName("Should create campaign")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void testCreateCampaign() throws Exception {
        String campaignId = "NEW-ONE";
        String content = """
                {
                   "campaign":"NEW-ONE",
                   "campaignLabel":"An other campaign",
                   "visibilities":[
                      {
                         "managementStartDate":1721683250000,
                         "interviewerStartDate":1721683251000,
                         "identificationPhaseStartDate":1721683252000,
                         "collectionStartDate":1721683253000,
                         "collectionEndDate":1721683254000,
                         "endDate":1721683255000,
                         "organizationalUnit":"OU-NORTH"
                      },
                      {
                         "managementStartDate":1721683260000,
                         "interviewerStartDate":1721683261000,
                         "identificationPhaseStartDate":1721683262000,
                         "collectionStartDate":1721683263000,
                         "collectionEndDate":1721683264000,
                         "endDate":1721683265000,
                         "organizationalUnit":"OU-SOUTH"
                      }
                   ],
                   "referents":[
                      {
                         "firstName":"Bob",
                         "lastName":"Marley",
                         "phoneNumber":"0123456789",
                         "role":"PRIMARY"
                      },
                      {
                         "firstName":"Mylene",
                         "lastName":"Mikoton",
                         "phoneNumber":"2345678901",
                         "role":"PRIMARY"
                      }
                   ],
                   "communications": [
                      {
                         "meshuggahId": "meshId1",
                         "medium": "LETTER",
                         "type": "REMINDER"
                      },
                      {
                         "meshuggahId": "meshId2",
                         "medium": "EMAIL",
                         "type": "NOTICE"
                      },
                      {
                         "meshuggahId": "meshId3",
                         "medium": "LETTER",
                         "type": "NOTICE"
                      }
                   ],
                   "email": "test.email@plop.com",
                   "identificationConfiguration":"IASCO",
                   "contactOutcomeConfiguration":"F2F",
                   "contactAttemptConfiguration":"F2F",
                   "communicationRequestConfiguration": null
                }
                """;
        mockMvc.perform(post(Constants.API_CAMPAIGN)
                        .with(authentication(AuthenticatedUserTestHelper.AUTH_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());

        Optional<Campaign> campaignOptional = campaignRepository.findById(campaignId);

        assertThat(campaignOptional).isPresent();
        Campaign campaignCreated = campaignOptional.get();
        assertCampaignInfos(campaignCreated, campaignId, "An other campaign", "test.email@plop.com",
                false, ContactAttemptConfiguration.F2F, IdentificationConfiguration.IASCO,
                ContactOutcomeConfiguration.F2F);
        assertThat(campaignCreated.getVisibilities()).hasSize(2);
        assertThat(campaignCreated.getVisibilities())
                .anySatisfy(visibilityToCheck -> assertVisibility(visibilityToCheck, campaignId, "OU-NORTH",
                        1721683250000L, 1721683251000L, 1721683252000L,
                        1721683253000L, 1721683254000L, 1721683255000L))
                .anySatisfy(visibilityToCheck -> assertVisibility(visibilityToCheck, campaignId, "OU-NORTH",
                        1721683250000L, 1721683251000L, 1721683252000L,
                        1721683253000L, 1721683254000L, 1721683255000L));

        assertThat(campaignCreated.getCommunicationTemplates()).hasSize(3);
        assertThat(campaignCreated.getCommunicationTemplates())
                .anySatisfy(communicationTemplateToCheck -> assertCommunicationTemplate(communicationTemplateToCheck, campaignId, "meshId1",
                        CommunicationMedium.LETTER, CommunicationType.REMINDER))
                .anySatisfy(communicationTemplateToCheck -> assertCommunicationTemplate(communicationTemplateToCheck, campaignId, "meshId2",
                        CommunicationMedium.EMAIL, CommunicationType.NOTICE))
                .anySatisfy(communicationTemplateToCheck -> assertCommunicationTemplate(communicationTemplateToCheck, campaignId, "meshId3",
                        CommunicationMedium.LETTER, CommunicationType.NOTICE));
        assertThat(campaignCreated.getReferents()).hasSize(2);

        assertThat(campaignCreated.getReferents())
                .anySatisfy(referentToCheck -> assertReferent(referentToCheck, campaignId, "PRIMARY",
                        "Marley", "Bob", "0123456789"))
                .anySatisfy(referentToCheck -> assertReferent(referentToCheck, campaignId, "PRIMARY",
                        "Mikoton", "Mylene", "2345678901"));
    }

    private void assertCommunicationTemplate(CommunicationTemplateDB communicationTemplateToCheck, String campaignId,
                                             String meshuggahId, CommunicationMedium medium, CommunicationType type) {
        assertThat(communicationTemplateToCheck.getMeshuggahId()).isEqualTo(meshuggahId);
        assertThat(communicationTemplateToCheck.getCampaign().getId()).isEqualTo(campaignId);
        assertThat(communicationTemplateToCheck.getType()).isEqualTo(type);
        assertThat(communicationTemplateToCheck.getMedium()).isEqualTo(medium);
    }

    @Test
    @DisplayName("Should update campaign")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void testUpdateCampaign() throws Exception {
        String campaignId = "SIMPSONS2020X00";

        String content = """
                {
                   "campaignLabel":"An other campaign",
                   "visibilities":[
                      {
                         "managementStartDate":1721683250000,
                         "interviewerStartDate":1721683251000,
                         "identificationPhaseStartDate":1721683252000,
                         "collectionStartDate":1721683253000,
                         "collectionEndDate":1721683254000,
                         "endDate":1721683255000,
                         "organizationalUnit":"OU-NORTH"
                      },
                      {
                         "managementStartDate":1721683260000,
                         "interviewerStartDate":1721683261000,
                         "identificationPhaseStartDate":1721683262000,
                         "collectionStartDate":1721683263000,
                         "collectionEndDate":1721683264000,
                         "endDate":1721683265000,
                         "organizationalUnit":"OU-SOUTH"
                      }
                   ],
                   "referents":[
                      {
                         "firstName":"Bob",
                         "lastName":"Marley",
                         "phoneNumber":"0123456789",
                         "role":"PRIMARY"
                      },
                      {
                         "firstName":"Dupont",
                         "lastName":"Jean",
                         "phoneNumber":"1234567890",
                         "role":"PRIMARY"
                      }
                   ],
                   "email":"test.test@sdf.com",
                   "identificationConfiguration":"NOIDENT",
                   "contactOutcomeConfiguration":"TEL",
                   "contactAttemptConfiguration":"F2F",
                   "communicationRequestConfiguration":true
                }
        """;
        mockMvc.perform(put("/api/campaign/" + campaignId)
                        .with(authentication(AuthenticatedUserTestHelper.AUTH_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());

        Optional<Campaign> campaignOptional = campaignRepository.findById(campaignId);

        assertThat(campaignOptional).isPresent();
        Campaign campaignUpdated = campaignOptional.get();
        assertCampaignInfos(campaignUpdated, campaignId, "An other campaign", "test.test@sdf.com",
                true, ContactAttemptConfiguration.F2F, IdentificationConfiguration.NOIDENT,
                ContactOutcomeConfiguration.TEL);
        assertThat(campaignUpdated.getVisibilities()).hasSize(2);
        assertThat(campaignUpdated.getVisibilities())
                .anySatisfy(visibilityToCheck -> assertVisibility(visibilityToCheck, campaignId, "OU-NORTH",
                        1721683250000L, 1721683251000L,
                        1721683252000L, 1721683253000L,
                        1721683254000L, 1721683255000L))
                .anySatisfy(visibilityToCheck -> assertVisibility(visibilityToCheck, campaignId, "OU-NORTH",
                        1721683250000L, 1721683251000L,
                        1721683252000L, 1721683253000L,
                        1721683254000L, 1721683255000L));

        assertThat(campaignUpdated.getReferents()).hasSize(2);
        assertThat(campaignUpdated.getReferents())
                .anySatisfy(referentToCheck -> assertReferent(referentToCheck, campaignId, "PRIMARY",
                        "Marley", "Bob", "0123456789"))
                .anySatisfy(referentToCheck -> assertReferent(referentToCheck, campaignId, "PRIMARY",
                        "Jean", "Dupont", "1234567890"));
    }

    @Test
    @DisplayName("Should delete campaign")
    @Sql(value = ScriptConstants.REINIT_SQL_SCRIPT, executionPhase = AFTER_TEST_METHOD)
    void testDeleteCampaign() throws Exception {
        String campaignId = "SIMPSONS2020X00";
        mockMvc.perform(delete("/api/campaign/" + campaignId)
                        .param("force", "true")
                        .with(authentication(AuthenticatedUserTestHelper.AUTH_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Optional<Campaign> campaignOptional = campaignRepository.findById(campaignId);
        assertThat(campaignOptional).isEmpty();
    }

    private void assertVisibility(VisibilityDB visibilityToCheck, String campaignId, String organizationUnitId,
                                  long managementStartDate, long interviewerStartDate,
                                  long identificationPhaseStartDate, long collectionStartDate,
                                  long collectionEndDate, long endDate) {
        assertThat(visibilityToCheck.getCampaign().getId()).isEqualTo(campaignId);
        assertThat(visibilityToCheck.getOrganizationUnit().getId()).isEqualTo(organizationUnitId);
        assertThat(visibilityToCheck.getManagementStartDate()).isEqualTo(managementStartDate);
        assertThat(visibilityToCheck.getInterviewerStartDate()).isEqualTo(interviewerStartDate);
        assertThat(visibilityToCheck.getIdentificationPhaseStartDate()).isEqualTo(identificationPhaseStartDate);
        assertThat(visibilityToCheck.getCollectionStartDate()).isEqualTo(collectionStartDate);
        assertThat(visibilityToCheck.getCollectionEndDate()).isEqualTo(collectionEndDate);
        assertThat(visibilityToCheck.getEndDate()).isEqualTo(endDate);
    }

    private void assertReferent(Referent referentToCheck, String campaignId, String role, String lastName,
                                String firstName, String phoneNumber) {
        assertThat(referentToCheck.getCampaign().getId()).isEqualTo(campaignId);
        assertThat(referentToCheck.getId()).isNotNull();
        assertThat(referentToCheck.getRole()).isEqualTo(role);
        assertThat(referentToCheck.getLastName()).isEqualTo(lastName);
        assertThat(referentToCheck.getFirstName()).isEqualTo(firstName);
        assertThat(referentToCheck.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    private void assertCampaignInfos(Campaign campaignToCheck, String campaignId, String label, String email,
                                     boolean communicationConfiguration, ContactAttemptConfiguration contactAttemptConfig,
                                     IdentificationConfiguration identificationConfig, ContactOutcomeConfiguration contactOutcomeConfig) {
        assertThat(campaignToCheck.getId()).isEqualTo(campaignId);
        assertThat(campaignToCheck.getLabel()).isEqualTo(label);
        assertThat(campaignToCheck.getEmail()).isEqualTo(email);
        assertThat(campaignToCheck.getCommunicationConfiguration()).isEqualTo(communicationConfiguration);
        assertThat(campaignToCheck.getContactAttemptConfiguration()).isEqualTo(contactAttemptConfig);
        assertThat(campaignToCheck.getIdentificationConfiguration()).isEqualTo(identificationConfig);
        assertThat(campaignToCheck.getContactOutcomeConfiguration()).isEqualTo(contactOutcomeConfig);
    }
}
