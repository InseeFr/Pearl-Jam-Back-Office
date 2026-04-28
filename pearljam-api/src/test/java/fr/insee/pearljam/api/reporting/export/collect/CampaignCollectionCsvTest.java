package fr.insee.pearljam.api.reporting.export.collect;

import fr.insee.pearljam.api.reporting.response.CampaignCollectionResponse;
import fr.insee.pearljam.api.reporting.response.ClosingCausesProgressResponse;
import fr.insee.pearljam.api.reporting.response.CollectionRatesResponse;
import fr.insee.pearljam.api.reporting.response.ContactOutcomesProgressResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignCollectionCsvTest {

    private static final CollectionRatesResponse RATES = new CollectionRatesResponse(50f, 25f, 10f);
    private static final ContactOutcomesProgressResponse OUTCOMES =
            new ContactOutcomesProgressResponse(1L, 2L, 3L, 4L, 10L);
    private static final ClosingCausesProgressResponse CLOSING_CAUSES =
            new ClosingCausesProgressResponse(5L, 6L, 11L);

    @Test
    void shouldHaveCampaignLabelAsFirstHeader() {
        CampaignCollectionCsv csv = CampaignCollectionCsv.from(List.of());
        assertThat(csv.headers().values().getFirst()).isEqualTo("Enquête");
    }

    @Test
    void shouldHaveAllExpectedHeadersInOrder() {
        CampaignCollectionCsv csv = CampaignCollectionCsv.from(List.of());

        assertThat(csv.headers().values()).hasSize(13);
        assertThat(csv.headers().values()).containsExactly(
                CollectCsvHeaders.CAMPAIGN_LABEL.getHeaderName(),
                CollectCsvHeaders.COLLECTION_RATE.getHeaderName(),
                CollectCsvHeaders.WASTE_RATE.getHeaderName(),
                CollectCsvHeaders.OUT_OF_SCOPE_RATE.getHeaderName(),
                CollectCsvHeaders.ACCEPTED.getHeaderName(),
                CollectCsvHeaders.REFUSED.getHeaderName(),
                CollectCsvHeaders.UNREACHABLE.getHeaderName(),
                CollectCsvHeaders.OUT_OF_SCOPE.getHeaderName(),
                CollectCsvHeaders.TOTAL_OUTCOMES.getHeaderName(),
                CollectCsvHeaders.ABSENCE_INTERVIEWER.getHeaderName(),
                CollectCsvHeaders.OTHER_REASONS.getHeaderName(),
                CollectCsvHeaders.TOTAL_CLOSED.getHeaderName(),
                CollectCsvHeaders.ALLOCATED.getHeaderName()
        );
    }

    @Test
    void shouldReturnEmptyRows_whenNoCampaigns() {
        CampaignCollectionCsv csv = CampaignCollectionCsv.from(List.of());
        assertThat(csv.rows()).isEmpty();
    }

    @Test
    void shouldMapCampaignToRow() {
        CampaignCollectionCsv csv = CampaignCollectionCsv.from(List.of(
                new CampaignCollectionResponse("camp-1", "Enquête 1", 100L, RATES, OUTCOMES, CLOSING_CAUSES)
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
        CampaignCollectionCsv csv = CampaignCollectionCsv.from(List.of(
                new CampaignCollectionResponse("camp-1", "Enquête 1", 100L, RATES, OUTCOMES, CLOSING_CAUSES),
                new CampaignCollectionResponse("camp-2", "Enquête 2", 50L, RATES, OUTCOMES, CLOSING_CAUSES)
        ));

        assertThat(csv.rows()).hasSize(2);
        assertThat(csv.rows().get(0).values().getFirst()).isEqualTo("Enquête 1");
        assertThat(csv.rows().get(1).values().getFirst()).isEqualTo("Enquête 2");
    }

    @Test
    void shouldHaveRowSizeMatchingHeaderSize() {
        CampaignCollectionCsv csv = CampaignCollectionCsv.from(List.of(
                new CampaignCollectionResponse("camp-1", "Enquête 1", 100L, RATES, OUTCOMES, CLOSING_CAUSES)
        ));

        assertThat(csv.rows().getFirst().values()).hasSameSizeAs(csv.headers().values());
    }
}
