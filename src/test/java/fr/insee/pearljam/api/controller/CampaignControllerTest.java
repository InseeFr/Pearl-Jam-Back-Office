package fr.insee.pearljam.api.controller;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import fr.insee.pearljam.api.utils.dummy.AuthenticationUserFakeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.insee.pearljam.api.service.dummy.CampaignFakeService;
import fr.insee.pearljam.api.service.dummy.ReferentFakeService;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignControllerTest {

    private CampaignController campaignController;
    private CampaignFakeService campaignService;
    private ReferentFakeService referentService;
    private AuthenticationUserFakeService authenticatedUserService;

    @BeforeEach
    public void init() {
        Campaign campaign = new Campaign();
        campaignService = new CampaignFakeService(campaign);
        referentService = new ReferentFakeService();
        authenticatedUserService = new AuthenticationUserFakeService(AuthenticatedUserTestHelper.AUTH_ADMIN);
        campaignController = new CampaignController(campaignService, referentService, authenticatedUserService);
    }

    @Test
    @DisplayName("On deletion, when force is true, deletion is done")
    void testDeletion() {
        campaignController.deleteCampaignById( "test", true);
        assertThat(campaignService.isDeleted()).isTrue();
    }
}
