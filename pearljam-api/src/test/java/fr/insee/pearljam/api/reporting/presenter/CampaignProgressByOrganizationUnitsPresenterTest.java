package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignProgressByOrganizationUnitsResponse;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.OrganizationUnitDailyStats;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignProgressByOrganizationUnitsPresenterTest {

    private final CampaignProgressByOrganizationUnitsPresenter presenter =
            new CampaignProgressByOrganizationUnitsPresenter();

    @Test
    @DisplayName("Maps organization unit and campaign stats to progress response")
    void shouldMapOrganizationUnitAndCampaignStatsToProgressResponse() {
        // Given
        OrganizationUnitDailyStats organizationUnitStats = ReportingPresenterTestData.organizationUnitStats("OU North");
        CampaignDailyStats campaignStats = ReportingPresenterTestData.campaignStats("camp-1", "Campaign 1", 7L);

        // When
        CampaignProgressByOrganizationUnitsResponse result =
                presenter.present(List.of(organizationUnitStats), campaignStats);

        // Then
        assertThat(result.organizationUnits()).singleElement().satisfies(organizationUnit -> {
            assertThat(organizationUnit.organizationUnitLabel()).isEqualTo("OU North");
            assertThat(organizationUnit.progressRate()).isEqualTo(organizationUnitStats.getProgressStateRate());
            assertThat(organizationUnit.states().started()).isEqualTo(organizationUnitStats.getInsStateCount());
            assertThat(organizationUnit.communications().noticeLetter()).isEqualTo(organizationUnitStats.getNoticeCommunicationCount());
        });
        assertThat(result.campaign().progressRate()).isEqualTo(campaignStats.getProgressStateRate());
        assertThat(result.campaign().states().allocated()).isEqualTo(campaignStats.getAllocatedCount());
    }
}
