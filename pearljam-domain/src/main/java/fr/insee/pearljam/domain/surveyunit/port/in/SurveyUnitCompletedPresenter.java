package fr.insee.pearljam.domain.surveyunit.port.in;

import fr.insee.pearljam.domain.surveyunit.service.model.SurveyUnitCompleted;
import org.springframework.data.domain.Page;

public interface SurveyUnitCompletedPresenter<T> {
    T present(Page<SurveyUnitCompleted> surveyUnits);
}
