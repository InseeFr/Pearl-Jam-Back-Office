package fr.insee.pearljam.api.surveyunit.presenter;

import fr.insee.pearljam.api.surveyunit.response.SurveyUnitCompletedResponse;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitCompletedView;
import fr.insee.pearljam.domain.surveyunit.port.in.application.SurveyUnitCompletedPresenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SurveyUnitCompletedApiPresenter implements SurveyUnitCompletedPresenter<List<SurveyUnitCompletedResponse>> {

    @Value("${application.external.service.datacollection-ui-url}")
    private String datacollectionUiUrl;

    @Override
    public List<SurveyUnitCompletedResponse> present(List<SurveyUnitCompletedView> surveyUnits) {
        return  surveyUnits.stream().map(su ->
                new SurveyUnitCompletedResponse(
                        su.getSurveyUnitId(),
                        su.getSurveyUnitDisplayName(),
                        su.getInterviewerFirstName() + " " + su.getInterviewerLastName(),
                        su.getEndDate(),
                        su.getContactOutcome() != null
                                ? ContactOutcomeType.valueOf(su.getContactOutcome())
                                : null,
                        su.getClosingCauseType() != null
                                ? ClosingCauseType.valueOf(su.getClosingCauseType())
                                : null,
                        su.getRead(),
                        datacollectionUiUrl + "/review/interrogations/" + su.getSurveyUnitId(),
                        su.getComment()
                )
        ).toList();
    }
}

