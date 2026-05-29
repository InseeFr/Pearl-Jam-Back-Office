package fr.insee.pearljam.domain.reporting.port.out;

import fr.insee.pearljam.domain.surveyunit.service.model.SurveyUnitToReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Repository port for retrieving survey units to review with pagination and search capabilities.
 * This port provides native SQL pagination for better performance compared to manual pagination.
 */
public interface SurveyUnitToReviewRepositoryPort {

    /**
     * Retrieves paginated survey units to review with search capabilities.
     *
     * @param campaignIds list of campaign IDs to filter by
     * @param ouIds list of organization unit IDs to filter by
     * @param search search term for multi-field search (campaign label, SU id, interviewer name)
     * @param pageable pagination information (page, size, sort)
     * @return paginated results with SurveyUnitToReview read models
     */
    Page<SurveyUnitToReview> findSurveyUnitsToReview(
            List<String> campaignIds,
            List<String> ouIds,
            String search,
            Pageable pageable);
}