package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.response.InterviewerCampaignCollectionResponse;
import fr.insee.pearljam.api.reporting.response.ClosingCausesProgressResponse;
import fr.insee.pearljam.api.reporting.response.CollectionRatesResponse;
import fr.insee.pearljam.api.reporting.response.ContactOutcomesProgressResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InterviewerCampaignsCollectionCsvTest {

    private static final CollectionRatesResponse RATES = new CollectionRatesResponse(50f, 25f, 10f);
    private static final ContactOutcomesProgressResponse OUTCOMES =
            new ContactOutcomesProgressResponse(1L, 2L, 3L, 4L, 10L);
    private static final ClosingCausesProgressResponse CLOSING_CAUSES =
            new ClosingCausesProgressResponse(5L, 6L, 11L);

    @Test
    void shouldHaveCampaignLabelAsFirstHeader() {
        InterviewerCampaignsCollectionCsv csv = InterviewerCampaignsCollectionCsv.from(List.of());
        assertThat(csv.headers().values().getFirst()).isEqualTo("Enquête");
    }

    @Test
    void shouldHaveAllExpectedHeadersInOrder() {
        InterviewerCampaignsCollectionCsv csv = InterviewerCampaignsCollectionCsv.from(List.of());

        assertThat(csv.headers().values()).hasSize(13);
        assertThat(csv.headers().values()).containsExactly(
                CollectionCsvHeaders.CAMPAIGN_LABEL.getHeaderName(),
                CollectionCsvHeaders.COLLECTION_RATE.getHeaderName(),
                CollectionCsvHeaders.WASTE_RATE.getHeaderName(),
                CollectionCsvHeaders.OUT_OF_SCOPE_RATE.getHeaderName(),
                CollectionCsvHeaders.ACCEPTED.getHeaderName(),
                CollectionCsvHeaders.REFUSED.getHeaderName(),
                CollectionCsvHeaders.UNREACHABLE.getHeaderName(),
                CollectionCsvHeaders.OUT_OF_SCOPE.getHeaderName(),
                CollectionCsvHeaders.TOTAL_OUTCOMES.getHeaderName(),
                CollectionCsvHeaders.ABSENCE_INTERVIEWER.getHeaderName(),
                CollectionCsvHeaders.OTHER_REASONS.getHeaderName(),
                CollectionCsvHeaders.TOTAL_CLOSED.getHeaderName(),
                CollectionCsvHeaders.ALLOCATED.getHeaderName()
        );
    }

    @Test
    void shouldReturnEmptyRows_whenNoCampaigns() {
        InterviewerCampaignsCollectionCsv csv = InterviewerCampaignsCollectionCsv.from(List.of());
        assertThat(csv.rows()).isEmpty();
    }

    @Test
    void shouldMapCampaignToRow() {
        InterviewerCampaignsCollectionCsv csv = InterviewerCampaignsCollectionCsv.from(List.of(
                new InterviewerCampaignCollectionResponse(
                        "camp-1", "Enquête 1", 100L, RATES, OUTCOMES, CLOSING_CAUSES)
        ));

        assertThat(csv.rows()).hasSize(1);
        assertThat(csv.rows().getFirst().values()).containsExactly(
                "Enquête 1",
                "50.0", "25.0", "10.0",
                "1", "2", "3",
                "4", "10",
                "5", "6", "11",
                "100"
        );
    }

    @Test
    void shouldMapMultipleCampaigns() {
        InterviewerCampaignsCollectionCsv csv = InterviewerCampaignsCollectionCsv.from(List.of(
                new InterviewerCampaignCollectionResponse(
                        "camp-1", "Enquête 1", 100L, RATES, OUTCOMES, CLOSING_CAUSES),
                new InterviewerCampaignCollectionResponse(
                        "camp-2", "Enquête 2", 50L, RATES, OUTCOMES, CLOSING_CAUSES)
        ));

        assertThat(csv.rows()).hasSize(2);
        assertThat(csv.rows().get(0).values().getFirst()).isEqualTo("Enquête 1");
        assertThat(csv.rows().get(1).values().getFirst()).isEqualTo("Enquête 2");
    }

    @Test
    void shouldHaveRowSizeMatchingHeaderSize() {
        InterviewerCampaignsCollectionCsv csv = InterviewerCampaignsCollectionCsv.from(List.of(
                new InterviewerCampaignCollectionResponse(
                        "camp-1", "Enquête 1", 100L, RATES, OUTCOMES, CLOSING_CAUSES)
        ));

        assertThat(csv.rows().getFirst().values()).hasSameSizeAs(csv.headers().values());
    }
}
