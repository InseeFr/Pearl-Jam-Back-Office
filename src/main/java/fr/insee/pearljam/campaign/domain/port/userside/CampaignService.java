package fr.insee.pearljam.campaign.domain.port.userside;

import java.util.List;
import java.util.Optional;

import fr.insee.pearljam.campaign.domain.service.exception.*;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.output.CampaignResponseDto;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.input.CampaignUpdateDto;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.CampaignCommonsDto;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.CampaignSensitivityDto;

import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.entity.Campaign;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.input.CampaignCreateDto;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.CampaignDto;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.CountDto;
import fr.insee.pearljam.configuration.web.exception.NotFoundException;
import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.interviewer.InterviewerDto;

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
