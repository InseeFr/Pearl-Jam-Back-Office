package fr.insee.pearljam.domain.reporting.port.in;

import fr.insee.pearljam.domain.reporting.readmodel.SurveyUnitToReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Port interface for retrieving survey units to review.
 * This interface defines the contract for getting paginated survey units that need review.
 */
public interface SurveyUnitToReviewPort {

    /**
     * Retrieves a paginated list of survey units to review for a specific user.
     *
     * @param userId the ID of the user requesting the survey units
     * @param search optional search criteria
     * @param pageable pagination information
     * @param presenter the presenter to format the response
     * @param <T> the type of response returned by the presenter
     * @return a paginated response of survey units to review
     */
    <T> T getSurveyUnitsToReview(String userId, String search, Pageable pageable, SurveyUnitToReviewPresenter<T> presenter);
}