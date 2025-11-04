package fr.insee.pearljam.integration.campaign;

import fr.insee.pearljam.helper.AuthenticatedUserTestHelper;
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

@ActiveProfiles(profiles = {"auth", "test"})
@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class CommunicationTemplateIT {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetCommunicationTemplates() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/campaign/SIMPSONS2020X00/communication-templates")
                        .with(authentication(AuthenticatedUserTestHelper.AUTH_ADMIN))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        String contentResult = mvcResult.getResponse().getContentAsString();
        String expectedResult = """
            [
               {
                  "id": "mesh1",
                  "campaignId": "SIMPSONS2020X00",
                  "meshuggahId":"mesh1",
                  "medium":"EMAIL",
                  "type":"REMINDER"
               },
               {
                  "id": "mesh2",
                  "campaignId": "SIMPSONS2020X00",
                  "meshuggahId":"mesh2",
                  "medium":"LETTER",
                  "type":"NOTICE"
               }
            ]
        """;
        JSONAssert.assertEquals(expectedResult, contentResult, JSONCompareMode.NON_EXTENSIBLE);
    }
}
