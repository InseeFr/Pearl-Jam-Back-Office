package fr.insee.pearljam.api.surveyunit.presenter;

import fr.insee.pearljam.api.surveyunit.response.SurveyUnitCompletedResponse;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitCompletedPresenter;
import fr.insee.pearljam.domain.surveyunit.service.model.SurveyUnitCompleted;
import org.springframework.data.domain.Page;

import java.util.List;

public class SurveyUnitCompletedApiPresenter implements SurveyUnitCompletedPresenter<List<SurveyUnitCompletedResponse>> {

    @Override
    public List<SurveyUnitCompletedResponse> present(Page<SurveyUnitCompleted> surveyUnits) {
        return List.of();
    }
}

