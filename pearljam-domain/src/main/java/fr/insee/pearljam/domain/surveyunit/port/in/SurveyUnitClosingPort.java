package fr.insee.pearljam.domain.surveyunit.port.in;

import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SurveyUnitClosingPort {
    void addClosingCauseToMultipleSurveyUnits(List<String> surveyUnitId, ClosingCauseType type, boolean toClose);

    <T> T getSurveyUnitsToClose(String userId, SurveyUnitClosingPresenter<T> presenter);

    /**
     * Retrieves survey units eligible for closing with pagination support.
     *
     * @param userId the user identifier
     * @param presenter the presenter to transform results
     * @param pageable pagination information
     * @param <T> the return type
     * @return the presented result
     */
    <T> T getSurveyUnitsToClose(String userId, SurveyUnitClosingPresenter<T> presenter, Pageable pageable);

    void deleteClosingCauseBySurveyUnitId(String surveyUnitId);
}
