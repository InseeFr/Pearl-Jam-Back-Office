package fr.insee.pearljam.api.service;

import java.util.List;
import java.util.Optional;

import fr.insee.pearljam.api.campaign.dto.output.CampaignResponseDto;
import fr.insee.pearljam.api.campaign.dto.input.CampaignUpdateDto;
import fr.insee.pearljam.domain.campaign.model.Visibility;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.exception.OrganizationalUnitNotFoundException;
import fr.insee.pearljam.domain.exception.VisibilityNotFoundException;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.campaign.dto.input.CampaignCreateDto;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.count.CountDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.domain.exception.CampaignAlreadyExistException;

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
	List<InterviewerDto> getListInterviewers(String userId, String campaignId) throws NotFoundException;

	boolean isUserPreference(String userId, String campaignId);

	CountDto getNbSUAbandonedByCampaign(String userId, String campaignId) throws NotFoundException;

	CountDto getNbSUNotAttributedByCampaign(String userId, String campaignId) throws NotFoundException;

	void createCampaign(CampaignCreateDto campaignDto) throws CampaignAlreadyExistException, CampaignNotFoundException, OrganizationalUnitNotFoundException;

	Optional<Campaign> findById(String campaignId);

	void delete(Campaign campaign);

	void updateCampaign(String id, CampaignUpdateDto campaign) throws CampaignNotFoundException, VisibilityNotFoundException;

	boolean isCampaignOngoing(String id) throws CampaignNotFoundException;

	List<Visibility> findAllVisibilitiesByCampaign(String campaignId) throws CampaignNotFoundException;

	CampaignResponseDto getCampaignDtoById(String id) throws CampaignNotFoundException;

	void updateVisibility(Visibility visibilityToUpdate) throws VisibilityNotFoundException;
}
