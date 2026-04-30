package fr.insee.pearljam.api.reporting.export.campaignorganization;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignOrganizationCsvHeadersTest {

    @Test
    void shouldHaveCorrectHeaderNames() {
        assertThat(CampaignOrganizationCsvHeaders.INTERVIEWER_LABEL.getHeaderName())
                .isEqualTo("Nom Prénom Enquêteur");
        assertThat(CampaignOrganizationCsvHeaders.INTERVIEWER_ID.getHeaderName())
                .isEqualTo("Idep Enquêteur");
        assertThat(CampaignOrganizationCsvHeaders.SURVEY_UNITS_COUNT.getHeaderName())
                .isEqualTo("Nombre d'UE");
    }

    @Test
    void shouldHaveAllThreeHeaders() {
        assertThat(CampaignOrganizationCsvHeaders.values()).hasSize(3);
    }

    @Test
    void shouldHaveHeadersInCorrectOrder() {
        CampaignOrganizationCsvHeaders[] headers = CampaignOrganizationCsvHeaders.values();
        assertThat(headers[0]).isEqualTo(CampaignOrganizationCsvHeaders.INTERVIEWER_LABEL);
        assertThat(headers[1]).isEqualTo(CampaignOrganizationCsvHeaders.INTERVIEWER_ID);
        assertThat(headers[2]).isEqualTo(CampaignOrganizationCsvHeaders.SURVEY_UNITS_COUNT);
    }
}
