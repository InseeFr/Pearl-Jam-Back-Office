package fr.insee.pearljam.domain.surveyunit.port.serverside;

/**
 * Port interface for moved survey unit operations
 */
public interface MovedSurveyUnitRepository {
    /**
     * Update a survey unit that has been marked as moved.
     * This operation sets priority to true, clears states and adds INS state,
     * sets demenagementWeb to true, and clears persons.
     *
     * @param surveyUnitId the survey unit id
     */
    void updateMovedSurveyUnit(String surveyUnitId);
}