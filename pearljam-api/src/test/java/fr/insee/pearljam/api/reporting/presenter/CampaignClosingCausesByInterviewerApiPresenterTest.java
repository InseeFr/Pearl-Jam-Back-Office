package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignClosingCausesByInterviewersResponse;
import fr.insee.pearljam.api.reporting.response.CampaignClosingCausesByInterviewersResponse.Interviewer;
import fr.insee.pearljam.api.reporting.response.CampaignClosingCausesByInterviewersResponse.Interviewer.SurveyUnitsResponse;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CampaignClosingCausesByInterviewerApiPresenterTest {

    private CampaignClosingCausesByInterviewerPresenter presenter;

    @BeforeEach
    void setUp() {
        presenter = new CampaignClosingCausesByInterviewerPresenter();
    }

    // --- Helpers ---

    private InterviewerDailyStats mockInterviewer(
            String id, String firstName, String lastName,
            long allocated,
            long npa, long npi, long npx, long row
    ) {
        InterviewerDailyStats stats = mock(InterviewerDailyStats.class);
        when(stats.getInterviewerId()).thenReturn(id);
        when(stats.getInterviewerFirstName()).thenReturn(firstName);
        when(stats.getInterviewerLastName()).thenReturn(lastName);
        when(stats.getAllocatedCount()).thenReturn(allocated);
        when(stats.getNpaProvisionalClosingCauseCount()).thenReturn(npa);
        when(stats.getNpiProvisionalClosingCauseCount()).thenReturn(npi);
        when(stats.getNpxProvisionalClosingCauseCount()).thenReturn(npx);
        when(stats.getRowProvisionalClosingCauseCount()).thenReturn(row);
        when(stats.getTotalProvisionalClosingCauses()).thenReturn(npa + npi + npx + row);
        return stats;
    }

    // --- Tests ---

    @Test
    void present_shouldReturnSingleInterviewerWithCorrectFields() {
        InterviewerDailyStats interviewerStats = mockInterviewer(
                "int-01", "Alice", "Martin",
                100L,
                5L, 3L, 2L, 1L);


        CampaignClosingCausesByInterviewersResponse result =
                presenter.present(List.of(interviewerStats), null, null);

        assertThat(result.interviewers()).hasSize(1);

        Interviewer interviewer = result.interviewers().getFirst();
        assertThat(interviewer.interviewerId()).isEqualTo("int-01");
        assertThat(interviewer.interviewerLabel()).isEqualTo("Alice Martin");

        SurveyUnitsResponse su = interviewer.surveyUnits();
        assertThat(su.allocated()).isEqualTo(100L);

        SurveyUnitsResponse.ClosingCauseResponse cc = su.closingCauses();
        assertThat(cc.interviewerAbsence()).isEqualTo(5L);
        assertThat(cc.notProcessedByInterviewer()).isEqualTo(3L);
        assertThat(cc.exceptionalReason()).isEqualTo(2L);
        assertThat(cc.rightOfWithdrawal()).isEqualTo(1L);
        assertThat(cc.total()).isEqualTo(11L);
    }

    @Test
    void present_shouldReturnMultipleInterviewersAndComputeCorrectlySite() {
        InterviewerDailyStats int1 = mockInterviewer("int-01", "Alice", "Martin", 100L, 1L, 2L, 3L, 4L);
        InterviewerDailyStats int2 = mockInterviewer("int-02", "Bob", "Dupont", 50L, 0L, 1L, 0L, 2L);

        CampaignClosingCausesByInterviewersResponse result =
                presenter.present(List.of(int1, int2), null, null);

        assertThat(result.interviewers()).hasSize(2);
        assertThat(result.interviewers().getFirst().interviewerId()).isEqualTo("int-01");
        assertThat(result.interviewers().get(1).interviewerId()).isEqualTo("int-02");
        assertThat(result.interviewers().get(1).interviewerLabel()).isEqualTo("Bob Dupont");

        assertThat(result.total().closingCauses().rightOfWithdrawal()).isEqualTo(6L);
        assertThat(result.total().allocated()).isEqualTo(150L);

    }

    @Test
    void present_shouldReturnEmptyInterviewerListWhenNoneProvided() {
        CampaignClosingCausesByInterviewersResponse result =
                presenter.present(List.of(), null, null);

        assertThat(result.interviewers()).isEmpty();
    }

    @Test
    void present_shouldBuildInterviewerLabelFromFirstAndLastName() {
        InterviewerDailyStats stats = mockInterviewer("id-99", "Jean", "Dupuis", 10L, 0L, 0L, 0L, 0L);
        CampaignClosingCausesByInterviewersResponse result =
                presenter.present(List.of(stats), null, null);

        assertThat(result.interviewers().getFirst().interviewerLabel()).isEqualTo("Jean Dupuis");
    }

    @Test
    void present_shouldHandleZeroCounts() {
        InterviewerDailyStats stats = mockInterviewer("int-zero", "Zero", "Values", 0L, 0L, 0L, 0L, 0L);

        CampaignClosingCausesByInterviewersResponse result =
                presenter.present(List.of(stats), null, null);

        Interviewer interviewer = result.interviewers().getFirst();
        assertThat(interviewer.surveyUnits().allocated()).isZero();
        assertThat(interviewer.surveyUnits().closingCauses().total()).isZero();
        assertThat(result.total().allocated()).isZero();
        assertThat(result.total().closingCauses().total()).isZero();
    }

    @Test
    void present_shouldComputeSiteWithZeroValuesWhenNoInterviewers() {
        CampaignClosingCausesByInterviewersResponse result =
                presenter.present(List.of(), null, null);

        assertThat(result.total().allocated()).isZero();
        assertThat(result.total().closingCauses().total()).isZero();
    }
}