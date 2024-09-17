package fr.insee.pearljam.api.campaign.controller;

import fr.insee.pearljam.api.campaign.dto.input.CommunicationInformationUpdateDto;
import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import fr.insee.pearljam.api.utils.JsonTestHelper;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.api.utils.dummy.AuthenticationUserFakeService;
import fr.insee.pearljam.domain.campaign.service.dummy.CommunicationInformationFakeService;
import fr.insee.pearljam.domain.exception.CommunicationInformationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

class CommunicationInformationControllerTest {

    private MockMvc mockMvc;
    private CommunicationInformationFakeService communicationInformationService;
    private final String campaignId = "campaign-id";
    private final String organizationalUnitId = "ou-id";
    private final String updatePath = String.format("/api/campaign/%s/organizational-unit/%s/communication-information", campaignId, organizationalUnitId);

    @BeforeEach
    void setup() {
        communicationInformationService = new CommunicationInformationFakeService();
        AuthenticationUserFakeService authenticatedUserService = new AuthenticationUserFakeService(AuthenticatedUserTestHelper.AUTH_ADMIN);
        CommunicationInformationController communicationInformationController =
                new CommunicationInformationController(communicationInformationService, authenticatedUserService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(communicationInformationController)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    @DisplayName("Should return not found when communication information does not exist")
    void testUpdateCommunicationInformation01() throws Exception {
        communicationInformationService.setShouldThrowCommunicationInformationNotFoundException(true);
        CommunicationInformationUpdateDto communicationInformationToUpdate =
                new CommunicationInformationUpdateDto("addr", "mail", "0123456789");
        mockMvc.perform(put(updatePath)
                        .content(JsonTestHelper.toJson(communicationInformationToUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.NOT_FOUND, updatePath, CommunicationInformationNotFoundException.MESSAGE));
    }
}
