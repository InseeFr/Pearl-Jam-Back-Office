package fr.insee.pearljam.api.reporting.export.collect;

import fr.insee.pearljam.api.reporting.response.CampaignCollectionByInterviewersResponse;
import fr.insee.pearljam.api.reporting.response.ClosingCausesProgressResponse;
import fr.insee.pearljam.api.reporting.response.CollectionRatesResponse;
import fr.insee.pearljam.api.reporting.response.ContactOutcomesProgressResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InterviewerCollectCsvTest {

    private static final CollectionRatesResponse RATES = new CollectionRatesResponse(50f, 25f, 10f);
    private static final ContactOutcomesProgressResponse OUTCOMES =
            new ContactOutcomesProgressResponse(1L, 2L, 3L, 4L, 10L);
    private static final ClosingCausesProgressResponse CLOSING_CAUSES =
            new ClosingCausesProgressResponse(5L, 6L, 11L);

    @Test
    void shouldHaveInterviewerLabelThenIdAsFirstHeaders() {
        InterviewerCollectCsv csv = InterviewerCollectCsv.from(emptyResponse());
        assertThat(csv.headers().values().get(0)).isEqualTo("Nom prénom");
        assertThat(csv.headers().values().get(1)).isEqualTo("Idep");
    }

    @Test
    void shouldHaveAllExpectedHeadersInOrder() {
        InterviewerCollectCsv csv = InterviewerCollectCsv.from(emptyResponse());

        assertThat(csv.headers().values()).hasSize(14);
        assertThat(csv.headers().values()).containsExactly(
                CollectCsvHeaders.INTERVIEWER_LABEL.getHeaderName(),
                CollectCsvHeaders.INTERVIEWER_ID.getHeaderName(),
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
    void shouldReturnEmptyRows_whenNoInterviewers() {
        InterviewerCollectCsv csv = InterviewerCollectCsv.from(emptyResponse());
        assertThat(csv.rows()).isEmpty();
    }

    @Test
    void shouldMapInterviewerToRow() {
        CampaignCollectionByInterviewersResponse response = responseWith(List.of(
                new CampaignCollectionByInterviewersResponse.Interviewer(
                        "INT1", "Jane Doe", 100L, RATES, OUTCOMES, CLOSING_CAUSES)
        ));

        InterviewerCollectCsv csv = InterviewerCollectCsv.from(response);

        assertThat(csv.rows()).hasSize(1);
        assertThat(csv.rows().getFirst().values()).containsExactly(
                "Jane Doe",
                "INT1",
                "50.0", "25.0", "10.0",
                "1", "2", "3",
                "4", "10",
                "5", "6", "11",
                "100"
        );
    }

    @Test
    void shouldMapMultipleInterviewers() {
        CampaignCollectionByInterviewersResponse response = responseWith(List.of(
                new CampaignCollectionByInterviewersResponse.Interviewer(
                        "INT1", "Jane Doe", 100L, RATES, OUTCOMES, CLOSING_CAUSES),
                new CampaignCollectionByInterviewersResponse.Interviewer(
                        "INT2", "John Smith", 50L, RATES, OUTCOMES, CLOSING_CAUSES)
        ));

        InterviewerCollectCsv csv = InterviewerCollectCsv.from(response);

        assertThat(csv.rows()).hasSize(2);
        assertThat(csv.rows().get(0).values().get(0)).isEqualTo("Jane Doe");
        assertThat(csv.rows().get(0).values().get(1)).isEqualTo("INT1");
        assertThat(csv.rows().get(1).values().get(0)).isEqualTo("John Smith");
        assertThat(csv.rows().get(1).values().get(1)).isEqualTo("INT2");
    }

    @Test
    void shouldHaveRowSizeMatchingHeaderSize() {
        CampaignCollectionByInterviewersResponse response = responseWith(List.of(
                new CampaignCollectionByInterviewersResponse.Interviewer(
                        "INT1", "Jane Doe", 100L, RATES, OUTCOMES, CLOSING_CAUSES)
        ));

        InterviewerCollectCsv csv = InterviewerCollectCsv.from(response);

        assertThat(csv.rows().getFirst().values()).hasSameSizeAs(csv.headers().values());
    }

    private static CampaignCollectionByInterviewersResponse emptyResponse() {
        return responseWith(List.of());
    }

    private static CampaignCollectionByInterviewersResponse responseWith(
            List<CampaignCollectionByInterviewersResponse.Interviewer> interviewers) {
        return new CampaignCollectionByInterviewersResponse(
                interviewers,
                new CampaignCollectionByInterviewersResponse.OrganizationUnit(
                        0L, RATES, OUTCOMES, CLOSING_CAUSES),
                new CampaignCollectionByInterviewersResponse.Campaign(
                        0L, 0L, RATES, OUTCOMES, CLOSING_CAUSES)
        );
    }
}
