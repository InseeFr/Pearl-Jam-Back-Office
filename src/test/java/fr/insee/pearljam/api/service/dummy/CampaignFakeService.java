package fr.insee.pearljam.api.service.dummy;

import java.util.List;
import java.util.Optional;

import fr.insee.pearljam.api.campaign.dto.input.CampaignUpdateDto;
import fr.insee.pearljam.api.campaign.dto.output.CampaignResponseDto;
import fr.insee.pearljam.domain.campaign.model.Visibility;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.exception.VisibilityNotFoundException;
import lombok.RequiredArgsConstructor;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.campaign.dto.input.CampaignCreateDto;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.count.CountDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.domain.exception.CampaignAlreadyExistException;
import fr.insee.pearljam.api.service.CampaignService;
import lombok.Getter;
import lombok.Setter;

@RequiredArgsConstructor
public class CampaignFakeService implements CampaignService {

    @Getter
    private boolean deleted = false;

    @Setter
    private boolean shouldThrowCampaignAlreadyExistException = false;

    @Setter
    private boolean shouldThrowCampaignNotFoundException = false;

    @Setter
    private boolean shouldThrowVisibilityNotFoundException = false;

    @Getter
    private CampaignCreateDto campaignCreated = null;

    @Getter
    private CampaignUpdateDto campaignUpdated = null;

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
            throws CampaignAlreadyExistException {
        if(shouldThrowCampaignAlreadyExistException) {
            throw new CampaignAlreadyExistException();
        }
        campaignCreated = campaignDto;
    }

    @Override
    public Optional<Campaign> findById(String campaignId) {
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public void delete(Campaign campaign) {
        deleted = true;
    }

    @Override
    public void updateCampaign(String id, CampaignUpdateDto campaign) throws CampaignNotFoundException, VisibilityNotFoundException{
        if(shouldThrowCampaignNotFoundException) {
            throw new CampaignNotFoundException();
        }
        if(shouldThrowVisibilityNotFoundException) {
            throw new VisibilityNotFoundException();
        }
        campaignUpdated = campaign;
    }

    @Override
    public boolean isCampaignOngoing(String id) {
        throw new UnsupportedOperationException("Unimplemented method 'isCampaignOngoing'");
    }

    @Override
    public List<Visibility> findAllVisibilitiesByCampaign(String campaignId) {
        throw new UnsupportedOperationException("Unimplemented method 'findAllVisiblitiesByCampaign'");
    }

    @Override
    public CampaignResponseDto getCampaignDtoById(String id) {
        throw new UnsupportedOperationException("Unimplemented method 'getCampaignDtoById'");
    }

    @Override
    public void updateVisibility(Visibility visibilityToUpdate) throws VisibilityNotFoundException {
        throw new UnsupportedOperationException("Unimplemented method 'updateVisibility'");
    }
}
