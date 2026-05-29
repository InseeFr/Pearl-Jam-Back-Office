package fr.insee.pearljam.domain.surveyunit.port.in;

import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.SurveyUnitUpdateDto;

/**
 * Temporary service used for the full app refactoring to update survey unit infos
 */
public interface SurveyUnitUpdateService {
    /**
     * Update the SurveyUnit with details from Dto
     * @param surveyUnit existing survey unit to update
     * @param surveyUnitUpdateDto details to update
     */
    void updateSurveyUnitInfos(SurveyUnitDB surveyUnit, SurveyUnitUpdateDto surveyUnitUpdateDto);
}
