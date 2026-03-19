package fr.insee.pearljam.domain.campaign.port.in;

import java.util.List;
import java.util.Optional;

import fr.insee.pearljam.contracts.campaign.dto.output.CampaignResponseDto;
import fr.insee.pearljam.contracts.campaign.dto.input.CampaignUpdateDto;
import fr.insee.pearljam.contracts.campaign.dto.CampaignCommonsDto;
import fr.insee.pearljam.contracts.campaign.dto.CampaignPreferenceDto;
import fr.insee.pearljam.contracts.campaign.dto.CampaignSensitivityDto;
import fr.insee.pearljam.contracts.campaign.dto.PortalDataDto;
import fr.insee.pearljam.domain.campaign.service.exception.*;

import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import fr.insee.pearljam.contracts.campaign.dto.input.CampaignCreateDto;
import fr.insee.pearljam.contracts.campaign.dto.CampaignProjection;
import fr.insee.pearljam.contracts.campaign.dto.CountDto;

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
	 * @return {@link List} of {@link CampaignProjection}
	 */
	List<CampaignProjection> getPreferredCampaigns(String userId);

	/**
	 * Return a list of related campaigns for the user
	 * @param userId
	 * @return {@link List} of {@link CampaignPreferenceDto}
	 */
	List<CampaignPreferenceDto> getCampaignPreferences(String userId);

	/**
	 * @return {@link List} of {@link CampaignProjection}
	 */
	List<CampaignProjection> getAllCampaigns();

	/**
	 * @return {@link List} of {@link CampaignProjection}
	 */
	List<CampaignProjection> getInterviewerCampaigns(String userId);

	CountDto getNbSUAbandonedByCampaign(String userId, String campaignId) throws CampaignNotFoundException;

	CountDto getNbSUNotAttributedByCampaign(String userId, String campaignId) throws CampaignNotFoundException;

	void createCampaign(CampaignCreateDto campaignDto) throws CampaignAlreadyExistException, OrganizationalUnitNotFoundException, VisibilityHasInvalidDatesException;

	Optional<CampaignDB> findById(String campaignId);

	void delete(String campaignId, boolean force) throws CampaignNotFoundException, CampaignOnGoingException;

	void updateCampaign(String id, CampaignUpdateDto campaign) throws CampaignNotFoundException, VisibilityNotFoundException, VisibilityHasInvalidDatesException;

	boolean isCampaignOngoing(String id) throws CampaignNotFoundException;

	CampaignResponseDto getCampaignDtoById(String id) throws CampaignNotFoundException;

	List<CampaignSensitivityDto> getCampaignSensitivityDto();

	CampaignCommonsDto findCampaignCommonsById(String id) throws CampaignNotFoundException;

	List<CampaignCommonsDto> findCampaignsCommonsOngoing() throws CampaignNotFoundException;

	PortalDataDto findCampaignPortalData(String campaignId, String userId) throws CampaignNotFoundException;
}
