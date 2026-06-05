package fr.insee.pearljam.domain.surveyunit.port.in;

import org.springframework.data.domain.Pageable;

/**
 * Port interface for retrieving survey units assigned.
 * This interface defines the contract for getting paginated survey units that need review.
 */
public interface SurveyUnitAssignedPort {

    /**
     * Retrieves a paginated list of survey units assigned to the organization-unit of a specific user.
     *
     * @param userId the ID of the user requesting the survey units
     * @param campaignId optional campaign id selected by the user
     * @param search optional search criteria
     * @param pageable pagination information
     * @param viewed filter survey-unit viewed or not viewed
     * @param presenter the presenter to format the response
     * @param <T> the type of response returned by the presenter
     * @return a paginated response of survey units to review
     */
    <T> T getSurveyUnitsAssigned(String userId, String campaignId, String search, Boolean viewed, Pageable pageable, SurveyUnitAssignedPresenter<T> presenter);
}