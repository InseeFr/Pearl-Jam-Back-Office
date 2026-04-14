package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.response.CampaignProgressByOrganizationUnitsResponse;
import fr.insee.pearljam.api.reporting.response.CommunicationsProgressResponse;
import fr.insee.pearljam.api.reporting.response.StatesProgressResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizationUnitProgressCsvTest {

    private static final StatesProgressResponse STATES = new StatesProgressResponse(10, 2, 3, 4, 5, 6, 7, 8, 9, 1);
    private static final CommunicationsProgressResponse COMMUNICATIONS = new CommunicationsProgressResponse(11, 12);

    @Test
    void shouldHaveOrganizationUnitLabelAsFirstHeader() {
        OrganizationUnitProgressCsv csv = OrganizationUnitProgressCsv.from(emptyResponse());
        assertThat(csv.headers().values().getFirst()).isEqualTo("Site");
    }

    @Test
    void shouldHaveAllCommonHeadersAfterOrganizationUnitLabel() {
        OrganizationUnitProgressCsv csv = OrganizationUnitProgressCsv.from(emptyResponse());

        assertThat(csv.headers().values()).hasSize(14);
        assertThat(csv.headers().values()).containsExactly(
                ProgressCsvHeaders.ORGANIZATION_UNIT_LABEL.getHeaderName(),
                ProgressCsvHeaders.PROGRESS_RATE.getHeaderName(),
                ProgressCsvHeaders.ALLOCATED.getHeaderName(),
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

    @Test
    void shouldReturnEmptyRows_whenNoOrganizationUnits() {
        OrganizationUnitProgressCsv csv = OrganizationUnitProgressCsv.from(emptyResponse());
        assertThat(csv.rows()).isEmpty();
    }

    @Test
    void shouldMapOrganizationUnitToRow() {
        CampaignProgressByOrganizationUnitsResponse response = responseWith(
                List.of(new CampaignProgressByOrganizationUnitsResponse.OrganizationUnit("Site Paris", 75.5f, STATES, COMMUNICATIONS))
        );

        OrganizationUnitProgressCsv csv = OrganizationUnitProgressCsv.from(response);

        assertThat(csv.rows()).hasSize(1);
        assertThat(csv.rows().getFirst().values()).containsExactly(
                "Site Paris",
                "75.5",
                "10", "2", "3", "4", "5", "6",
                "7", "8", "9", "1",
                "11", "12"
        );
    }

    @Test
    void shouldMapMultipleOrganizationUnits() {
        CampaignProgressByOrganizationUnitsResponse response = responseWith(List.of(
                new CampaignProgressByOrganizationUnitsResponse.OrganizationUnit("Site Paris", 50f, STATES, COMMUNICATIONS),
                new CampaignProgressByOrganizationUnitsResponse.OrganizationUnit("Site Lyon", 80f, STATES, COMMUNICATIONS)
        ));

        OrganizationUnitProgressCsv csv = OrganizationUnitProgressCsv.from(response);

        assertThat(csv.rows()).hasSize(2);
        assertThat(csv.rows().get(0).values().getFirst()).isEqualTo("Site Paris");
        assertThat(csv.rows().get(1).values().getFirst()).isEqualTo("Site Lyon");
    }

    @Test
    void shouldHaveRowSizeMatchingHeaderSize() {
        CampaignProgressByOrganizationUnitsResponse response = responseWith(
                List.of(new CampaignProgressByOrganizationUnitsResponse.OrganizationUnit("Site Paris", 50f, STATES, COMMUNICATIONS))
        );

        OrganizationUnitProgressCsv csv = OrganizationUnitProgressCsv.from(response);

        assertThat(csv.rows().getFirst().values()).hasSameSizeAs(csv.headers().values());
    }

    private static CampaignProgressByOrganizationUnitsResponse emptyResponse() {
        return responseWith(List.of());
    }

    private static CampaignProgressByOrganizationUnitsResponse responseWith(
            List<CampaignProgressByOrganizationUnitsResponse.OrganizationUnit> organizationUnits) {
        return new CampaignProgressByOrganizationUnitsResponse(
                organizationUnits,
                new CampaignProgressByOrganizationUnitsResponse.Campaign(0f, STATES, COMMUNICATIONS)
        );
    }
}
