package fr.insee.pearljam.domain.campaign.port.in;

import fr.insee.pearljam.contracts.campaign.dto.*;
import fr.insee.pearljam.contracts.campaign.dto.input.CampaignCreateDto;
import fr.insee.pearljam.contracts.campaign.dto.input.CampaignUpdateDto;
import fr.insee.pearljam.contracts.campaign.dto.output.CampaignResponseDto;
import fr.insee.pearljam.domain.campaign.CampaignPreferenceModel;
import fr.insee.pearljam.domain.campaign.service.exception.*;
import fr.insee.pearljam.domain.reporting.readmodel.progress.CampaignPhase;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;

import java.util.List;
import java.util.Optional;

/**
 * Service for the Campaign entity
 *
 * @author scorcaud
 *
 */
public interface CampaignService {

    /**
     * Return a list of preferred campaigns for the user
     *
     * @param userId
     * @return {@link List} of {@link CampaignDto}
     */
    List<CampaignDto> getPreferredCampaigns(String userId);

    /**
     * Return a list of related campaigns for the user
     *
     * @param userId
     * @return {@link List} of {@link CampaignPreferenceDto}
     */
    List<CampaignPreferenceDto> getCampaignPreferences(String userId);

    /**
     * Return a list of related campaigns for the user
     *
     * @param userId
     * @param campaignPhase
     * @return {@link List} of {@link CampaignPreferenceModel}
     */
    List<CampaignPreferenceModel> getCampaignPreferencesForSpecificPhase(String userId, CampaignPhase campaignPhase);

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
