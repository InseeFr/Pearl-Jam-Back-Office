package fr.insee.pearljam.domain.closingcause.port.userside;

import fr.insee.pearljam.api.closingcause.dto.ClosingCauseCountDto;

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
