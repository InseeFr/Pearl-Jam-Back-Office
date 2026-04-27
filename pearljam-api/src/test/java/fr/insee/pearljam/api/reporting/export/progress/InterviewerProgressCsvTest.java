package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.response.CampaignProgressByInterviewersResponse;
import fr.insee.pearljam.api.reporting.response.CommunicationsProgressResponse;
import fr.insee.pearljam.api.reporting.response.StatesProgressResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InterviewerProgressCsvTest {

    private static final StatesProgressResponse STATES = new StatesProgressResponse(10, 2, 3, 4, 5, 6, 7, 8, 9, 1);
    private static final CommunicationsProgressResponse COMMUNICATIONS = new CommunicationsProgressResponse(11, 12);

    @Test
    void shouldHaveInterviewerLabelAsFirstHeader() {
        InterviewerProgressCsv csv = InterviewerProgressCsv.from(emptyResponse());
        assertThat(csv.headers().values().getFirst()).isEqualTo(ProgressCsvHeaders.INTERVIEWER_ID.getHeaderName());
    }

    @Test
    void shouldHaveAllCommonHeadersAfterInterviewerLabel() {
        InterviewerProgressCsv csv = InterviewerProgressCsv.from(emptyResponse());

        assertThat(csv.headers().values()).hasSize(15);
        assertThat(csv.headers().values()).containsExactly(
                ProgressCsvHeaders.INTERVIEWER_ID.getHeaderName(),
                ProgressCsvHeaders.INTERVIEWER_LABEL.getHeaderName(),
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
    void shouldReturnEmptyRows_whenNoInterviewers() {
        InterviewerProgressCsv csv = InterviewerProgressCsv.from(emptyResponse());
        assertThat(csv.rows()).isEmpty();
    }

    @Test
    void shouldMapInterviewerToRow() {
        CampaignProgressByInterviewersResponse response = responseWith(
                List.of(new CampaignProgressByInterviewersResponse.Interviewer("JDUP", "Jean Dupont", 75.5f, STATES, COMMUNICATIONS))
        );

        InterviewerProgressCsv csv = InterviewerProgressCsv.from(response);

        assertThat(csv.rows()).hasSize(1);
        assertThat(csv.rows().getFirst().values()).containsExactly(
                "JDUP",
                "Jean Dupont",
                "75.5",
                "10", "2", "3", "4", "5", "6",
                "7", "8", "9", "1",
                "11", "12"
        );
    }

    @Test
    void shouldMapMultipleInterviewers() {
        CampaignProgressByInterviewersResponse response = responseWith(List.of(
                new CampaignProgressByInterviewersResponse.Interviewer("JDUP", "Jean Dupont", 50f, STATES, COMMUNICATIONS),
                new CampaignProgressByInterviewersResponse.Interviewer("MMAR", "Marie Martin", 80f, STATES, COMMUNICATIONS)
        ));

        InterviewerProgressCsv csv = InterviewerProgressCsv.from(response);

        assertThat(csv.rows()).hasSize(2);
        assertThat(csv.rows().get(0).values().getFirst()).isEqualTo("JDUP");
        assertThat(csv.rows().get(1).values().getFirst()).isEqualTo("MMAR");
    }

    @Test
    void shouldHaveRowSizeMatchingHeaderSize() {
        CampaignProgressByInterviewersResponse response = responseWith(
                List.of(new CampaignProgressByInterviewersResponse.Interviewer("JDUP", "Jean Dupont", 50f, STATES, COMMUNICATIONS))
        );

        InterviewerProgressCsv csv = InterviewerProgressCsv.from(response);

        assertThat(csv.rows().getFirst().values()).hasSameSizeAs(csv.headers().values());
    }

    private static CampaignProgressByInterviewersResponse emptyResponse() {
        return responseWith(List.of());
    }

    private static CampaignProgressByInterviewersResponse responseWith(
            List<CampaignProgressByInterviewersResponse.Interviewer> interviewers) {
        return new CampaignProgressByInterviewersResponse(
                interviewers,
                new CampaignProgressByInterviewersResponse.OrganizationUnit(0f, STATES, COMMUNICATIONS),
                new CampaignProgressByInterviewersResponse.Campaign(0, 0f, STATES, COMMUNICATIONS)
        );
    }
}
