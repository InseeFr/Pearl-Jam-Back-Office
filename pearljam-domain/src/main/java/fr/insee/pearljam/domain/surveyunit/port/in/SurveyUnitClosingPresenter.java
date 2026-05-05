package fr.insee.pearljam.domain.surveyunit.port.in;

import fr.insee.pearljam.domain.surveyunit.model.QuestionnaireState;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;

import java.util.List;
import java.util.Map;

public interface SurveyUnitClosingPresenter<T> {
    T present(List<ClosableSurveyUnitView> projections,
              Map<String, ClosableSurveyUnitCandidateView> candidatesById,
              Map<String, QuestionnaireState> questionnaireStates);

    T empty();
}
