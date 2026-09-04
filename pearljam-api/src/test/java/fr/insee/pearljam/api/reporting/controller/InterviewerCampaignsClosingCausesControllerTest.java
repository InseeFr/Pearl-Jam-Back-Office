package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.presenter.InterviewerCampaignsClosingCausesPresenter;
import fr.insee.pearljam.api.reporting.response.InterviewerCampaignsClosingCausesResponse;
import fr.insee.pearljam.api.reporting.response.InterviewerCampaignsClosingCausesResponse.InterviewerCampaignSurveyUnits;
import fr.insee.pearljam.api.reporting.response.InterviewerCampaignsClosingCausesResponse.InterviewerCampaignsTotalSurveyUnit;
import fr.insee.pearljam.domain.reporting.port.in.InterviewerCampaignsReportingPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
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
        LocalDate day = LocalDate.of(2024, Month.JANUARY, 15);

        InterviewerCampaignSurveyUnits campaignSurveyUnits = new InterviewerCampaignSurveyUnits(
                "CAMPAIGN-1",
                10L,
                new InterviewerCampaignSurveyUnits.ClosingCauseResponse(1L, 2L, 3L, 4L, 10L),
                0L
        );
        InterviewerCampaignsTotalSurveyUnit total = new InterviewerCampaignsTotalSurveyUnit(
                10L,
                new InterviewerCampaignsTotalSurveyUnit.ClosingCauseResponse(1L, 2L, 3L, 4L, 10L),
                0L
        );
        InterviewerCampaignsClosingCausesResponse expected =
                new InterviewerCampaignsClosingCausesResponse(List.of(campaignSurveyUnits), total);

        when(interviewerCampaignsReportingPort.getCampaignsStatsForInterviewer(userId, day, interviewerId, presenter))
                .thenReturn(expected);

        InterviewerCampaignsClosingCausesResponse result =
                controller.getInterviewerClosingCausesByCampaign(interviewerId, day, userId);

        assertThat(result).isEqualTo(expected);
        verify(interviewerCampaignsReportingPort).getCampaignsStatsForInterviewer(userId, day, interviewerId, presenter);
    }

    @Test
    void getInterviewerClosingCausesByCampaign_shouldHandleNullDay() {
        String interviewerId = "interviewer-1";
        String userId = "user-1";

        InterviewerCampaignsClosingCausesResponse expected =
                new InterviewerCampaignsClosingCausesResponse(
                        List.of(),
                        new InterviewerCampaignsTotalSurveyUnit(
                                0L,
                                new InterviewerCampaignsTotalSurveyUnit.ClosingCauseResponse(0L, 0L, 0L, 0L, 0L),
                                0L
                        )
                );

        when(interviewerCampaignsReportingPort.getCampaignsStatsForInterviewer(userId, null, interviewerId, presenter))
                .thenReturn(expected);

        InterviewerCampaignsClosingCausesResponse result =
                controller.getInterviewerClosingCausesByCampaign(interviewerId, null, userId);

        assertThat(result).isEqualTo(expected);
        verify(interviewerCampaignsReportingPort).getCampaignsStatsForInterviewer(userId, null, interviewerId, presenter);
    }
}