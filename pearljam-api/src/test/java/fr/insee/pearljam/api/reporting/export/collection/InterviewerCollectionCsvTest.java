package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.response.CampaignCollectionByInterviewersResponse;
import fr.insee.pearljam.api.reporting.response.ClosingCausesProgressResponse;
import fr.insee.pearljam.api.reporting.response.CollectionRatesResponse;
import fr.insee.pearljam.api.reporting.response.ContactOutcomesProgressResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InterviewerCollectionCsvTest {

    private static final CollectionRatesResponse RATES = new CollectionRatesResponse(50f, 25f, 10f);
    private static final ContactOutcomesProgressResponse OUTCOMES =
            new ContactOutcomesProgressResponse(1L, 2L, 3L, 4L, 10L);
    private static final ClosingCausesProgressResponse CLOSING_CAUSES =
            new ClosingCausesProgressResponse(5L, 6L, 11L);

    @Test
    void shouldHaveInterviewerLabelThenIdAsFirstHeaders() {
        InterviewerCollectionCsv csv = InterviewerCollectionCsv.from(emptyResponse());
        assertThat(csv.headers().values().get(0)).isEqualTo(CollectionCsvHeaders.INTERVIEWER_LABEL.getHeaderName());
        assertThat(csv.headers().values().get(1)).isEqualTo(CollectionCsvHeaders.INTERVIEWER_ID.getHeaderName());
    }

    @Test
    void shouldHaveAllExpectedHeadersInOrder() {
        InterviewerCollectionCsv csv = InterviewerCollectionCsv.from(emptyResponse());

        assertThat(csv.headers().values()).hasSize(14);
        assertThat(csv.headers().values()).containsExactly(
                CollectionCsvHeaders.INTERVIEWER_LABEL.getHeaderName(),
                CollectionCsvHeaders.INTERVIEWER_ID.getHeaderName(),
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
    void shouldReturnEmptyRows_whenNoInterviewers() {
        InterviewerCollectionCsv csv = InterviewerCollectionCsv.from(emptyResponse());
        assertThat(csv.rows()).isEmpty();
    }

    @Test
    void shouldMapInterviewerToRow() {
        CampaignCollectionByInterviewersResponse response = responseWith(List.of(
                new CampaignCollectionByInterviewersResponse.Interviewer(
                        "INT1", "Jane Doe", 100L, RATES, OUTCOMES, CLOSING_CAUSES)
        ));

        InterviewerCollectionCsv csv = InterviewerCollectionCsv.from(response);

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

        InterviewerCollectionCsv csv = InterviewerCollectionCsv.from(response);

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

        InterviewerCollectionCsv csv = InterviewerCollectionCsv.from(response);

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
