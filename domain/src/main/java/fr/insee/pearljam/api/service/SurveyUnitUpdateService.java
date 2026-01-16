package fr.insee.pearljam.api.service;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.surveyunit.dto.SurveyUnitUpdateDto;

/**
 * Temporary service used for the full app refactoring to update survey unit infos
 */
public interface SurveyUnitUpdateService {
    /**
     * Update the SurveyUnit with details from Dto
     * @param surveyUnit existing survey unit to update
     * @param surveyUnitUpdateDto details to update
     */
    void updateSurveyUnitInfos(SurveyUnit surveyUnit, SurveyUnitUpdateDto surveyUnitUpdateDto);
}
