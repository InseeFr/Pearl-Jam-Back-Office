package fr.insee.pearljam.api.service;

import java.util.List;

import org.springframework.http.HttpStatus;

import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.dto.campaign.CampaignContextDto;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.campaign.CollectionDatesDto;
import fr.insee.pearljam.api.dto.count.CountDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.dto.visibility.VisibilityDto;
import fr.insee.pearljam.api.exception.NoOrganizationUnitException;
import fr.insee.pearljam.api.exception.VisibilityException;

/**
 * Service for the Campaign entity
 * 
 * @author scorcaud
 *
 */
public interface CampaignService {

	/**
	 * @param userId
	 * @return {@link List} of {@link CampaignDto} 
	 */
	List<CampaignDto> getListCampaign(String userId);

	/**
	 * @param userId
	 * @param campaignId
	 * @return {@link List} of {@link InterviewerDto}
	 */
	List<InterviewerDto> getListInterviewers(String userId, String campaignId);

	boolean isUserPreference(String userId, String campaignId);

	CountDto getNbSUAbandonedByCampaign(String userId, String campaignId);

	CountDto getNbSUNotAttributedByCampaign(String userId, String campaignId);

	HttpStatus updateDates(String userId, String id, CollectionDatesDto campaign);
	
	/**
	 * Update the visibility for a given campaign and a Organizational Unit
	 * @param idCampaign
	 * @param idOu
	 * @param updatedVisibility
	 * @return
	 */
	HttpStatus updateVisibility(String idCampaign, String idOu, VisibilityDto updatedVisibility);

	Response postCampaign(CampaignContextDto campaignDto) throws NoOrganizationUnitException, VisibilityException;
}
