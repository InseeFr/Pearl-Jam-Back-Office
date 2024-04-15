package fr.insee.pearljam.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.insee.pearljam.api.service.dummy.CampaignFakeService;
import fr.insee.pearljam.api.service.dummy.ReferentFakeService;
import static fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper.*;
import fr.insee.pearljam.api.utils.dummy.AuthenticationFakeHelper;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignControllerTest {

    private CampaignController campaignController;
    private CampaignFakeService campaignService;
    private ReferentFakeService referentService;
    private AuthenticationFakeHelper authenticationHelper;

    @BeforeEach
    public void init() {
        authenticationHelper = new AuthenticationFakeHelper();
        campaignService = new CampaignFakeService();
        referentService = new ReferentFakeService();
    }

    @Test
    @DisplayName("On deletion, when force is true, deletion is done")
    void testDeletion() {
        campaignController = new CampaignController(campaignService, authenticationHelper, referentService);
        campaignController.deleteCampaignById(AUTH_MANAGER, "test", true);
        assertThat(campaignService.isDeleted()).isTrue();
    }
}
