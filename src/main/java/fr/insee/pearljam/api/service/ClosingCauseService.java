package fr.insee.pearljam.api.service;

import fr.insee.pearljam.api.dto.closingcause.ClosingCauseCountDto;

import java.util.List;

/**
 * Service for the Campaign entity
 * 
 * @author scorcaud
 *
 */
public interface ClosingCauseService {
	ClosingCauseCountDto getClosingCauseCount(String userId, String campaignId, String interviewerId, Long date,
											  List<String> associatedOrgUnits);
}
