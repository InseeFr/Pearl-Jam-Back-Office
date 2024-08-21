package fr.insee.pearljam.integration.surveyunit;

import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ActiveProfiles("auth")
@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class SurveyUnitIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetAllSurveyUnits() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/survey-units")
                        .with(authentication(AuthenticatedUserTestHelper.AUTH_INTERVIEWER))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        String contentResult = mvcResult.getResponse().getContentAsString();
        String expectedResult = """
            [
               {
                  "id":"11",
                  "campaign":"SIMPSONS2020X00",
                  "campaignLabel":"Survey on the Simpsons tv show 2020",
                  "managementStartDate":1718966154301,
                  "interviewerStartDate":1719052554302,
                  "identificationPhaseStartDate":1719138954303,
                  "collectionStartDate":1719225354304,
                  "collectionEndDate":1721903754305,
                  "endDate":1724582154306,
                  "identificationConfiguration":"IASCO",
                  "contactOutcomeConfiguration":"F2F",
                  "contactAttemptConfiguration":"F2F",
                  "communicationRequestConfiguration":false
               },
               {
                  "id":"12",
                  "campaign":"SIMPSONS2020X00",
                  "campaignLabel":"Survey on the Simpsons tv show 2020",
                  "managementStartDate":1718966154301,
                  "interviewerStartDate":1719052554302,
                  "identificationPhaseStartDate":1719138954303,
                  "collectionStartDate":1719225354304,
                  "collectionEndDate":1721903754305,
                  "endDate":1724582154306,
                  "identificationConfiguration":"IASCO",
                  "contactOutcomeConfiguration":"F2F",
                  "contactAttemptConfiguration":"F2F",
                  "communicationRequestConfiguration":false
               },
               {
                  "id":"20",
                  "campaign":"VQS2021X00",
                  "campaignLabel":"Everyday life and health survey 2021",
                  "managementStartDate":1718966154308,
                  "interviewerStartDate":1719052554308,
                  "identificationPhaseStartDate":1719138954308,
                  "collectionStartDate":1719225354308,
                  "collectionEndDate":1721903754308,
                  "endDate":1724582154308,
                  "identificationConfiguration":"IASCO",
                  "contactOutcomeConfiguration":"TEL",
                  "contactAttemptConfiguration":"TEL",
                  "communicationRequestConfiguration":false
               }
            ]
        """;
        JSONAssert.assertEquals(expectedResult, contentResult, JSONCompareMode.NON_EXTENSIBLE);
    }
}
