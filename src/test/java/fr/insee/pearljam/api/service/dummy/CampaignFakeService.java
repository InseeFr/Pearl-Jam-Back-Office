package fr.insee.pearljam.api.service.dummy;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.dto.campaign.CampaignContextDto;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.count.CountDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.dto.visibility.VisibilityContextDto;
import fr.insee.pearljam.api.dto.visibility.VisibilityDto;
import fr.insee.pearljam.api.exception.NoOrganizationUnitException;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.exception.VisibilityException;
import fr.insee.pearljam.api.service.CampaignService;
import lombok.Getter;

@Getter
public class CampaignFakeService implements CampaignService {

    private boolean deleted = false;

    @Override
    public List<CampaignDto> getListCampaign(String userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getListCampaign'");
    }

    @Override
    public List<CampaignDto> getAllCampaigns() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllCampaigns'");
    }

    @Override
    public List<CampaignDto> getInterviewerCampaigns(String userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getInterviewerCampaigns'");
    }

    @Override
    public List<InterviewerDto> getListInterviewers(String userId, String campaignId) throws NotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getListInterviewers'");
    }

    @Override
    public boolean isUserPreference(String userId, String campaignId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isUserPreference'");
    }

    @Override
    public CountDto getNbSUAbandonedByCampaign(String userId, String campaignId) throws NotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNbSUAbandonedByCampaign'");
    }

    @Override
    public CountDto getNbSUNotAttributedByCampaign(String userId, String campaignId) throws NotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNbSUNotAttributedByCampaign'");
    }

    @Override
    public HttpStatus updateVisibility(String idCampaign, String idOu, VisibilityDto updatedVisibility) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateVisibility'");
    }

    @Override
    public Response postCampaign(CampaignContextDto campaignDto)
            throws NoOrganizationUnitException, VisibilityException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'postCampaign'");
    }

    @Override
    public Optional<Campaign> findById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public void delete(Campaign campaign) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public HttpStatus updateCampaign(String id, CampaignContextDto campaign) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateCampaign'");
    }

    @Override
    public boolean isCampaignOngoing(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isCampaignOngoing'");
    }

    @Override
    public List<VisibilityContextDto> findAllVisiblitiesByCampaign(String campaignId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAllVisiblitiesByCampaign'");
    }

    @Override
    public void persistReferents(CampaignContextDto campaignDto, Campaign campaign) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'persistReferents'");
    }

    @Override
    public CampaignContextDto getCampaignDtoById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCampaignDtoById'");
    }

    @Override
    public boolean existsAny() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'existsAny'");
    }

}
