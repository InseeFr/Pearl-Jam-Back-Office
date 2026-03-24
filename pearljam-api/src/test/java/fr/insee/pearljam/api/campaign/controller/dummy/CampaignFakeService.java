package fr.insee.pearljam.api.campaign.controller.dummy;

import fr.insee.pearljam.contracts.campaign.dto.input.CampaignCreateDto;
import fr.insee.pearljam.contracts.campaign.dto.input.CampaignUpdateDto;
import fr.insee.pearljam.contracts.campaign.dto.output.CampaignResponseDto;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import fr.insee.pearljam.contracts.campaign.dto.CampaignCommonsDto;
import fr.insee.pearljam.contracts.campaign.dto.CampaignDto;
import fr.insee.pearljam.contracts.campaign.dto.CampaignPreferenceDto;
import fr.insee.pearljam.contracts.campaign.dto.CampaignSensitivityDto;
import fr.insee.pearljam.contracts.campaign.dto.PortalDataDto;
import fr.insee.pearljam.contracts.campaign.dto.CountDto;
import fr.insee.pearljam.domain.campaign.port.in.CampaignService;
import fr.insee.pearljam.domain.campaign.service.exception.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CampaignFakeService implements CampaignService {

    @Getter
    private boolean deleted = false;

    @Getter
    private boolean deleteForced = false;

    @Setter
    private boolean shouldThrowCampaignAlreadyExistException = false;

    @Setter
    private boolean shouldThrowCampaignNotFoundException = false;

    @Setter
    private boolean shouldThrowCampaignOnGoingException = false;

    @Setter
    private boolean shouldThrowVisibilityNotFoundException = false;

    @Setter
    private boolean shouldThrowVisibilityHasInvalidDatesException = false;

    @Setter
    private boolean shouldThrowOrganizationalUnitNotFoundException = false;

    @Getter
    private CampaignCreateDto campaignCreated = null;

    @Getter
    private CampaignUpdateDto campaignUpdated = null;

    @Setter
    private PortalDataDto portalDataToReturn = null;

    @Setter
    private CampaignResponseDto campaignToRetrieve = null;

    @Override
    public List<CampaignDto> getPreferredCampaigns(String userId) {
        return List.of();
    }

    @Override
    public List<CampaignPreferenceDto> getCampaignPreferences(String userId) {
        return List.of();
    }

    @Override
    public List<CampaignDto> getAllCampaigns() {
        throw new UnsupportedOperationException("Unimplemented method 'getAllCampaigns'");
    }

    @Override
    public List<CampaignDto> getInterviewerCampaigns(String userId) {
        throw new UnsupportedOperationException("Unimplemented method 'getInterviewerCampaigns'");
    }

    @Override
    public CountDto getNbSUAbandonedByCampaign(String userId, String campaignId) {
        throw new UnsupportedOperationException("Unimplemented method 'getNbSUAbandonedByCampaign'");
    }

    @Override
    public CountDto getNbSUNotAttributedByCampaign(String userId, String campaignId) {
        throw new UnsupportedOperationException("Unimplemented method 'getNbSUNotAttributedByCampaign'");
    }

    @Override
    public void createCampaign(CampaignCreateDto campaignDto)
            throws CampaignAlreadyExistException, OrganizationalUnitNotFoundException, VisibilityHasInvalidDatesException {
        if(shouldThrowCampaignAlreadyExistException) {
            throw new CampaignAlreadyExistException();
        }
        if(shouldThrowOrganizationalUnitNotFoundException) {
            throw new OrganizationalUnitNotFoundException();
        }
        if(shouldThrowVisibilityHasInvalidDatesException) {
            throw new VisibilityHasInvalidDatesException();
        }
        campaignCreated = campaignDto;
    }

    @Override
    public Optional<CampaignDB> findById(String campaignId) {
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public void delete(String campaignId, boolean force) throws CampaignNotFoundException, CampaignOnGoingException {
        deleteForced = force;
        if(shouldThrowCampaignNotFoundException) {
            throw new CampaignNotFoundException();
        }
        if(shouldThrowCampaignOnGoingException) {
            throw new CampaignOnGoingException();
        }
        deleted = true;
    }

    @Override
    public void updateCampaign(String id, CampaignUpdateDto campaign) throws CampaignNotFoundException, VisibilityNotFoundException, VisibilityHasInvalidDatesException {
        if(shouldThrowCampaignNotFoundException) {
            throw new CampaignNotFoundException();
        }
        if(shouldThrowVisibilityNotFoundException) {
            throw new VisibilityNotFoundException();
        }
        if(shouldThrowVisibilityHasInvalidDatesException) {
            throw new VisibilityHasInvalidDatesException();
        }
        campaignUpdated = campaign;
    }

    @Override
    public boolean isCampaignOngoing(String id) {
        throw new UnsupportedOperationException("Unimplemented method 'isCampaignOngoing'");
    }

    @Override
    public CampaignResponseDto getCampaignDtoById(String campaignId) throws CampaignNotFoundException{
        if(shouldThrowCampaignNotFoundException) {
            throw new CampaignNotFoundException();
        }
        return campaignToRetrieve;
    }

    @Override
    public List<CampaignSensitivityDto> getCampaignSensitivityDto() {
        return List.of();
    }

    @Override
    public CampaignCommonsDto findCampaignCommonsById(String id) {
        return null;
    }

    @Override
    public List<CampaignCommonsDto> findCampaignsCommonsOngoing() {
        return List.of();
    }

    @Override
    public PortalDataDto findCampaignPortalData(String campaignId, String userId) throws CampaignNotFoundException {
        if(shouldThrowCampaignNotFoundException) {
            throw new CampaignNotFoundException();
        }
        return portalDataToReturn;
    }
}
