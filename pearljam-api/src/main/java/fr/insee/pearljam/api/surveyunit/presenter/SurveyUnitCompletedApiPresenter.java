package fr.insee.pearljam.api.surveyunit.presenter;

import fr.insee.pearljam.api.surveyunit.response.SurveyUnitCompletedResponse;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import fr.insee.pearljam.domain.surveyunit.port.in.application.SurveyUnitCompletedPresenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SurveyUnitCompletedApiPresenter implements SurveyUnitCompletedPresenter<List<SurveyUnitCompletedResponse>> {

    @Value("${application.external.service.datacollection-ui-url}")
    private String datacollectionUiUrl;

    @Override
    public List<SurveyUnitCompletedResponse> present(Page<SurveyUnitFetchedByStatesAndCampaignIdView> surveyUnits) {
        return  surveyUnits.stream().map(su ->
                new SurveyUnitCompletedResponse(
                        su.surveyUnitId(),
                        su.surveyUnitDisplayName(),
                        su.interviewerFirstName() + " " + su.interviewerLastName(),
                        su.endDate(),
                        su.contactOutcome() != null
                                ? ContactOutcomeType.valueOf(su.contactOutcome())
                                : null,
                        su.closingCauseType() != null
                                ? ClosingCauseType.valueOf(su.closingCauseType())
                                : null,
                        su.viewed(),
                        datacollectionUiUrl + "/review/interrogations/" + su.surveyUnitId(),
                        su.comment()
                )
        ).toList();
    }
}

