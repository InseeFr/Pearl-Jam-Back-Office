package fr.insee.pearljam.api.reporting.export.progress;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizationUnitProgressCsvTest {

    @Test
    @DisplayName("Has the organization unit label column as the first header")
    void shouldHaveOrganizationUnitLabelAsFirstHeader() {
        // Given / When
        OrganizationUnitProgressCsv csv = new OrganizationUnitProgressCsv(List.of());

        // Then
        assertThat(csv.headers().values().getFirst()).isEqualTo("Site");
    }

    @Test
    @DisplayName("Exposes all common progress headers in order after the organization unit label")
    void shouldHaveAllCommonHeadersAfterOrganizationUnitLabel() {
        // Given / When
        OrganizationUnitProgressCsv csv = new OrganizationUnitProgressCsv(List.of());

        // Then
        assertThat(csv.headers().values()).hasSize(14);
        assertThat(csv.headers().values()).containsExactly(
                ProgressCsvHeaders.ORGANIZATION_UNIT_LABEL.getHeaderName(),
                ProgressCsvHeaders.PROGRESS_RATE.getHeaderName(),
                ProgressCsvHeaders.ALLOCATED_INTERVIEWER.getHeaderName(),
                ProgressCsvHeaders.NOT_STARTED.getHeaderName(),
                ProgressCsvHeaders.IN_PROGRESS.getHeaderName(),
                ProgressCsvHeaders.PENDING_TRANSMISSION.getHeaderName(),
                ProgressCsvHeaders.TO_REVIEW.getHeaderName(),
                ProgressCsvHeaders.VALIDATED.getHeaderName(),
                ProgressCsvHeaders.PREPARING_CONTACT.getHeaderName(),
                ProgressCsvHeaders.WITH_CONTACT.getHeaderName(),
                ProgressCsvHeaders.WITH_APPOINTMENT.getHeaderName(),
                ProgressCsvHeaders.STARTED.getHeaderName(),
                ProgressCsvHeaders.NOTICE_LETTER.getHeaderName(),
                ProgressCsvHeaders.REMINDER_LETTER.getHeaderName()
        );
    }
}
