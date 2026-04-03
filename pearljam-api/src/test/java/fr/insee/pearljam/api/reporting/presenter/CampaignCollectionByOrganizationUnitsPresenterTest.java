package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignCollectionByOrganizationUnitsResponse;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.OrganizationUnitDailyStats;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignCollectionByOrganizationUnitsPresenterTest {

    private final CampaignCollectionByOrganizationUnitsPresenter presenter =
            new CampaignCollectionByOrganizationUnitsPresenter();

    @Test
    void shouldMapOrganizationUnitAndCampaignStatsToCollectionResponse() {
        OrganizationUnitDailyStats organizationUnitStats = ReportingPresenterTestData.organizationUnitStats("OU North");
        CampaignDailyStats campaignStats = ReportingPresenterTestData.campaignStats("camp-1", "Campaign 1", 7L);

        CampaignCollectionByOrganizationUnitsResponse result =
                presenter.present(List.of(organizationUnitStats), campaignStats);

        assertThat(result.organizationUnits()).singleElement().satisfies(organizationUnit -> {
            assertThat(organizationUnit.organizationUnitLabel()).isEqualTo("OU North");
            assertThat(organizationUnit.allocated()).isEqualTo(organizationUnitStats.getAllocatedStateCount());
            assertThat(organizationUnit.rates().collection()).isEqualTo(organizationUnitStats.getCollectionRate());
            assertThat(organizationUnit.outcomes().accepted()).isEqualTo(organizationUnitStats.getInaContactOutcomeCount());
            assertThat(organizationUnit.closingCauses().totalClosed()).isEqualTo(organizationUnitStats.getTotalClosingCauses());
        });
        assertThat(result.campaign().allocated()).isEqualTo(campaignStats.getAllocatedStateCount());
        assertThat(result.campaign().rates().outOfScope()).isEqualTo(campaignStats.getOutOfScopeRate());
    }
}
