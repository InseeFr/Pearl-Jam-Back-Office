package fr.insee.pearljam.domain.surveyunit.port.out;

import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitAssigned;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Repository port for retrieving survey units assigned with pagination and search capabilities.
 * This port provides native SQL pagination for better performance compared to manual pagination.
 */
public interface SurveyUnitAssignedRepositoryPort {

    /**
     * Retrieves paginated survey units to review with search capabilities.
     *
     * @param campaignIds list of campaign IDs to filter by
     * @param search search term for multi-field search (campaign label, SU id, interviewer name)
     * @param pageable pagination information (page, size, sort)
     * @return paginated results with SurveyUnitAssigned read models
     */
    Page<SurveyUnitAssigned> findSurveyUnitsAssigned(
            List<String> campaignIds,
            String search,
            Pageable pageable);
}