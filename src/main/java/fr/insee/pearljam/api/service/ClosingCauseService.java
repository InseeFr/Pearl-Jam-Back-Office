package fr.insee.pearljam.api.service;

import java.util.List;

import fr.insee.pearljam.api.dto.closingcause.ClosingCauseCountDto;
import fr.insee.pearljam.api.exception.NotFoundException;

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
