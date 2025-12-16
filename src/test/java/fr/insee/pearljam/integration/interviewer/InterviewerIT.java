package fr.insee.pearljam.integration.interviewer;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import fr.insee.pearljam.config.FixedDateServiceConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
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
@Import(FixedDateServiceConfiguration.class)
@Transactional
class InterviewerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return interviewers associated to the supervisor")
    void testGetInterviewers() throws Exception {
        MvcResult mvcResult =
                mockMvc.perform(get(Constants.API_INTERVIEWERS).with(authentication(AuthenticatedUserTestHelper.AUTH_LOCAL_USER)).accept(MediaType.APPLICATION_JSON)).andReturn();

        String contentResult = mvcResult.getResponse().getContentAsString();
        String expectedResult = """
			[
			   {
				  "id":"INTW2",
				  "interviewerFirstName":"Carlton",
				  "interviewerLastName":"Campbell"
			   },
			   {
				  "id":"INTW4",
				  "interviewerFirstName":"Melody",
				  "interviewerLastName":"Grant"
			   },
			   {
				  "id":"INTW1",
				  "interviewerFirstName":"Margie",
				  "interviewerLastName":"Lucas"
			   },
			   {
				  "id":"INTW3",
				  "interviewerFirstName":"Gerald",
				  "interviewerLastName":"Edwards"
			   }
			]""";
        JSONAssert.assertEquals(expectedResult, contentResult, JSONCompareMode.NON_EXTENSIBLE);
    }
}
