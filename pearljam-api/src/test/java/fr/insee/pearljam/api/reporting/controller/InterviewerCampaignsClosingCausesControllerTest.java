package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.presenter.InterviewerCampaignsClosingCausesPresenter;
import fr.insee.pearljam.api.reporting.response.InterviewerCampaignsClosingCausesResponse;
import fr.insee.pearljam.domain.reporting.port.in.InterviewerCampaignsReportingPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterviewerCampaignsClosingCausesControllerTest {

    @Mock
    private InterviewerCampaignsReportingPort interviewerCampaignsReportingPort;

    @Mock
    private InterviewerCampaignsClosingCausesPresenter presenter;

    @InjectMocks
    private InterviewerCampaignsClosingCausesController controller;

    @Test
    void getInterviewerClosingCausesByCampaign_shouldDelegateToPortAndReturnResult() {
        String interviewerId = "interviewer-1";
        String userId = "user-1";
        LocalDate day = LocalDate.of(2024, 1, 15);

        List<InterviewerCampaignsClosingCausesResponse> expected = List.of(
                new InterviewerCampaignsClosingCausesResponse("CAMPAIGN-1", 10L,
                        new InterviewerCampaignsClosingCausesResponse.ClosingCauseResponse(1L, 2L, 3L, 4L, 10L))
        );
        when(interviewerCampaignsReportingPort.getCampaignsStatsForInterviewer(userId, day, interviewerId, presenter))
                .thenReturn(expected);

        List<InterviewerCampaignsClosingCausesResponse> result =
                controller.getInterviewerClosingCausesByCampaign(interviewerId, day, userId);

        assertThat(result).isEqualTo(expected);
        verify(interviewerCampaignsReportingPort).getCampaignsStatsForInterviewer(userId, day, interviewerId, presenter);
    }

    @Test
    void getInterviewerClosingCausesByCampaign_shouldHandleNullDay() {
        String interviewerId = "interviewer-1";
        String userId = "user-1";

        when(interviewerCampaignsReportingPort.getCampaignsStatsForInterviewer(userId, null, interviewerId, presenter))
                .thenReturn(List.of());

        List<InterviewerCampaignsClosingCausesResponse> result =
                controller.getInterviewerClosingCausesByCampaign(interviewerId, null, userId);

        assertThat(result).isEmpty();
        verify(interviewerCampaignsReportingPort).getCampaignsStatsForInterviewer(userId, null, interviewerId, presenter);
    }
}