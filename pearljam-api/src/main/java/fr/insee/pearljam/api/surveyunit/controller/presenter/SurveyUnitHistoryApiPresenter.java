package fr.insee.pearljam.api.surveyunit.controller.presenter;

import fr.insee.pearljam.api.surveyunit.response.SurveyUnitHistoryResponse;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitHistoryPresenter;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitHistory;
import org.springframework.stereotype.Component;

@Component
public class SurveyUnitHistoryApiPresenter implements SurveyUnitHistoryPresenter<SurveyUnitHistoryResponse> {

    @Override
    public SurveyUnitHistoryResponse present(SurveyUnitHistory surveyUnitHistory) {
        return new SurveyUnitHistoryResponse(surveyUnitHistory.surveyUnitId(),
                surveyUnitHistory.surveyUnitDisplayName(),
                surveyUnitHistory.states(),
                surveyUnitHistory.communications()
        );
    }
}
