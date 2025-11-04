package fr.insee.pearljam.surveyunit.domain.port.userside;

import java.util.List;

import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.closingcause.ClosingCauseCountDto;
import fr.insee.pearljam.configuration.web.exception.NotFoundException;

/**
 * Service for the Campaign entity
 * 
 * @author scorcaud
 *
 */
public interface ClosingCauseService {
	ClosingCauseCountDto getClosingCauseCount(String userId, String campaignId, String interviewerId, Long date,
			List<String> associatedOrgUnits) throws NotFoundException;
}
