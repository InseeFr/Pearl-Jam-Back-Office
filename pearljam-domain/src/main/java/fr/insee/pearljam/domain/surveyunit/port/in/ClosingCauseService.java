package fr.insee.pearljam.domain.surveyunit.port.in;

import fr.insee.pearljam.api.surveyunit.dto.closingcause.ClosingCauseCountDto;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;

import java.util.List;

/**
 * Service for the Campaign entity
 * 
 * @author scorcaud
 *
 */
public interface ClosingCauseService {
	ClosingCauseCountDto getClosingCauseCount(String userId, String campaignId, String interviewerId, Long date,
											  List<String> associatedOrgUnits) throws CampaignNotFoundException;
}
