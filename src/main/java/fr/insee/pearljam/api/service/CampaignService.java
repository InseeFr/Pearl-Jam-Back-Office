package fr.insee.pearljam.api.service;

import java.util.List;
import java.util.Optional;

import fr.insee.pearljam.api.campaign.dto.output.CampaignResponseDto;
import fr.insee.pearljam.api.campaign.dto.input.CampaignUpdateDto;
import fr.insee.pearljam.api.dto.campaign.CampaignCommonsDto;
import fr.insee.pearljam.api.dto.campaign.CampaignPreferenceDto;
import fr.insee.pearljam.api.dto.campaign.CampaignSensitivityDto;
import fr.insee.pearljam.api.dto.campaign.PortalDataDto;
import fr.insee.pearljam.domain.exception.*;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.campaign.dto.input.CampaignCreateDto;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.count.CountDto;

/**
 * Service for the Campaign entity
 * 
 * @author scorcaud
 *
 */
public interface CampaignService {

	/**
	 * Return a list of preferred campaigns for the user
	 * @param userId
	 * @return {@link List} of {@link CampaignDto}
	 */
	List<CampaignDto> getPreferredCampaigns(String userId);

	/**
	 * Return a list of related campaigns for the user
	 * @param userId
	 * @return {@link List} of {@link CampaignPreferenceDto}
	 */
	List<CampaignPreferenceDto> getCampaignPreferences(String userId);

	/**
	 * @return {@link List} of {@link CampaignDto}
	 */
	List<CampaignDto> getAllCampaigns();

	/**
	 * @return {@link List} of {@link CampaignDto}
	 */
	List<CampaignDto> getInterviewerCampaigns(String userId);

	CountDto getNbSUAbandonedByCampaign(String userId, String campaignId) throws CampaignNotFoundException;

	CountDto getNbSUNotAttributedByCampaign(String userId, String campaignId) throws CampaignNotFoundException;

	void createCampaign(CampaignCreateDto campaignDto) throws CampaignAlreadyExistException, OrganizationalUnitNotFoundException, VisibilityHasInvalidDatesException;

	Optional<Campaign> findById(String campaignId);

	void delete(String campaignId, boolean force) throws CampaignNotFoundException, CampaignOnGoingException;

	void updateCampaign(String id, CampaignUpdateDto campaign) throws CampaignNotFoundException, VisibilityNotFoundException, VisibilityHasInvalidDatesException;

	boolean isCampaignOngoing(String id) throws CampaignNotFoundException;

	CampaignResponseDto getCampaignDtoById(String id) throws CampaignNotFoundException;

	List<CampaignSensitivityDto> getCampaignSensitivityDto();

	CampaignCommonsDto findCampaignCommonsById(String id) throws CampaignNotFoundException;

	List<CampaignCommonsDto> findCampaignsCommonsOngoing() throws CampaignNotFoundException;

	PortalDataDto findCampaignPortalData(String campaignId, String userId) throws CampaignNotFoundException;
}
