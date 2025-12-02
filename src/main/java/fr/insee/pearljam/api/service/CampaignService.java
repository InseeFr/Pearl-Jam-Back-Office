package fr.insee.pearljam.api.service;

import java.util.List;
import java.util.Optional;

import fr.insee.pearljam.api.campaign.dto.output.CampaignResponseDto;
import fr.insee.pearljam.api.campaign.dto.input.CampaignUpdateDto;
import fr.insee.pearljam.api.dto.campaign.CampaignCommonsDto;
import fr.insee.pearljam.api.dto.campaign.CampaignSensitivityDto;
import fr.insee.pearljam.domain.exception.*;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.campaign.dto.input.CampaignCreateDto;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.count.CountDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.exception.NotFoundException;

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
	 * @return {@link List} of {@link CampaignDto}
	 */
	List<CampaignDto> getAllCampaigns();

	/**
	 * @return {@link List} of {@link CampaignDto}
	 */
	List<CampaignDto> getInterviewerCampaigns(String userId);

	/**
	 * @param userId
	 * @param campaignId
	 * @return {@link List} of {@link InterviewerDto}
	 * @throws NotFoundException
	 */
    boolean isUserPreference(String userId, String campaignId);

	CountDto getNbSUAbandonedByCampaign(String userId, String campaignId) throws NotFoundException;

	CountDto getNbSUNotAttributedByCampaign(String userId, String campaignId) throws NotFoundException;

	void createCampaign(CampaignCreateDto campaignDto) throws CampaignAlreadyExistException, OrganizationalUnitNotFoundException, VisibilityHasInvalidDatesException;

	Optional<Campaign> findById(String campaignId);

	void delete(String campaignId, boolean force) throws CampaignNotFoundException, CampaignOnGoingException;

	void updateCampaign(String id, CampaignUpdateDto campaign) throws CampaignNotFoundException, VisibilityNotFoundException, VisibilityHasInvalidDatesException, OrganizationalUnitNotFoundException;

	boolean isCampaignOngoing(String id) throws CampaignNotFoundException;

	CampaignResponseDto getCampaignDtoById(String id) throws CampaignNotFoundException;

	List<CampaignSensitivityDto> getCampaignSensitivityDto() throws CampaignNotFoundException;

	CampaignCommonsDto findCampaignCommonsById(String id) throws CampaignNotFoundException;

	List<CampaignCommonsDto> findCampaignsCommonsOngoing() throws CampaignNotFoundException;
}
