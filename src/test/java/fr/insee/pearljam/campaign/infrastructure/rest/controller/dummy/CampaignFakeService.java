package fr.insee.pearljam.campaign.infrastructure.rest.controller.dummy;

import java.util.List;
import java.util.Optional;

import fr.insee.pearljam.campaign.infrastructure.rest.dto.input.CampaignUpdateDto;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.output.CampaignResponseDto;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.CampaignCommonsDto;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.CampaignSensitivityDto;
import fr.insee.pearljam.campaign.domain.service.exception.*;
import lombok.RequiredArgsConstructor;

import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.entity.Campaign;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.input.CampaignCreateDto;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.CampaignDto;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.CountDto;
import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.configuration.web.exception.NotFoundException;
import fr.insee.pearljam.campaign.domain.port.userside.CampaignService;
import lombok.Getter;
import lombok.Setter;

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
    private CampaignResponseDto campaignToRetrieve = null;

    @Override
    public List<CampaignDto> getListCampaign(String userId) {
        throw new UnsupportedOperationException("Unimplemented method 'getListCampaign'");
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
    public List<InterviewerDto> getListInterviewers(String userId, String campaignId) throws NotFoundException {
        throw new UnsupportedOperationException("Unimplemented method 'getListInterviewers'");
    }

    @Override
    public boolean isUserPreference(String userId, String campaignId) {
        throw new UnsupportedOperationException("Unimplemented method 'isUserPreference'");
    }

    @Override
    public CountDto getNbSUAbandonedByCampaign(String userId, String campaignId) throws NotFoundException {
        throw new UnsupportedOperationException("Unimplemented method 'getNbSUAbandonedByCampaign'");
    }

    @Override
    public CountDto getNbSUNotAttributedByCampaign(String userId, String campaignId) throws NotFoundException {
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
    public Optional<Campaign> findById(String campaignId) {
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
    public CampaignCommonsDto findCampaignCommonsById(String id) throws CampaignNotFoundException {
        return null;
    }

    @Override
    public List<CampaignCommonsDto> findCampaignsCommonsOngoing() throws CampaignNotFoundException {
        return List.of();
    }
}
