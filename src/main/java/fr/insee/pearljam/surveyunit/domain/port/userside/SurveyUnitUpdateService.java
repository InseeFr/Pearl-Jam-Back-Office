package fr.insee.pearljam.surveyunit.domain.port.userside;

import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.SurveyUnit;
import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.SurveyUnitUpdateDto;

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
