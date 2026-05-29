package fr.insee.pearljam.domain.surveyunit.port.in.application;

import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitCompletedView;
import java.util.List;

public interface SurveyUnitCompletedPresenter<T> {
    T present(List<SurveyUnitCompletedView> surveyUnits);
}
