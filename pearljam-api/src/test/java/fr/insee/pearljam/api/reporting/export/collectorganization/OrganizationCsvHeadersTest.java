package fr.insee.pearljam.api.reporting.export.collectorganization;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizationCsvHeadersTest {

    @Test
    void shouldHaveCorrectHeaderNames() {
        assertThat(OrganizationCsvHeaders.INTERVIEWER_LABEL.getHeaderName())
                .isEqualTo("Nom Prénom Enquêteur");
        assertThat(OrganizationCsvHeaders.INTERVIEWER_ID.getHeaderName())
                .isEqualTo("Idep Enquêteur");
        assertThat(OrganizationCsvHeaders.SURVEY_UNITS_COUNT.getHeaderName())
                .isEqualTo("Nombre d'UE");
    }

    @Test
    void shouldHaveAllThreeHeaders() {
        assertThat(OrganizationCsvHeaders.values()).hasSize(3);
    }

    @Test
    void shouldHaveHeadersInCorrectOrder() {
        OrganizationCsvHeaders[] headers = OrganizationCsvHeaders.values();
        assertThat(headers[0]).isEqualTo(OrganizationCsvHeaders.INTERVIEWER_LABEL);
        assertThat(headers[1]).isEqualTo(OrganizationCsvHeaders.INTERVIEWER_ID);
        assertThat(headers[2]).isEqualTo(OrganizationCsvHeaders.SURVEY_UNITS_COUNT);
    }
}
