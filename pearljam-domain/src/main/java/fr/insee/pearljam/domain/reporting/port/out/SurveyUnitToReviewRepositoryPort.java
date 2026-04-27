package fr.insee.pearljam.domain.reporting.port.out;

import fr.insee.pearljam.domain.reporting.readmodel.SurveyUnitToReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Repository port for retrieving survey units to review.
 * This interface defines the contract for data access operations related to survey units
 * that need to be reviewed.
 */
public interface SurveyUnitToReviewRepositoryPort {

    /**
     * Retrieves a paginated list of survey units to review for a specific user.
     *
     * @param userId the ID of the user
     * @param search optional search criteria
     * @param pageable pagination information
     * @return a paginated list of survey units to review
     */
    Page<SurveyUnitToReview> findSurveyUnitsToReview(String userId, String search, Pageable pageable);

    /**
     * Retrieves survey units to review for a specific user without pagination.
     *
     * @param userId the ID of the user
     * @param search optional search criteria
     * @return a list of survey units to review
     */
    List<SurveyUnitToReview> findSurveyUnitsToReview(String userId, String search);
}