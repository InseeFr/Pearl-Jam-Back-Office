package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.model.CampaignOrganization;
import fr.insee.pearljam.domain.campaign.port.in.CampaignOrganizationPort;
import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.campaign.port.out.CampaignOrganizationRepository;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignWithVisibility;
import fr.insee.pearljam.domain.reporting.readmodel.Interviewer;
import fr.insee.pearljam.domain.reporting.readmodel.Referent;
import fr.insee.pearljam.domain.reporting.readmodel.SurveyUnitsCampaignOrganization;
import fr.insee.pearljam.domain.reporting.model.CampaignPhase;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CampaignOrganizationService implements CampaignOrganizationPort {

    private final CampaignOrganizationRepository campaignOrganizationRepository;
    private final DateService dateService;
    @Override
    public CampaignOrganization getCampaignOrganizations(String userId, String campaignId) {

        long currentTimestamp = dateService.getCurrentTimestamp();
        CampaignWithVisibility campaign = campaignOrganizationRepository.findCampaignVisibility(campaignId);
        List<Referent> referents = campaignOrganizationRepository.getReferents(campaignId);
        List<Interviewer> interviewers = campaignOrganizationRepository.getInterviewers(campaignId);
        SurveyUnitsCampaignOrganization surveyUnits = campaignOrganizationRepository.getSurveyUnitsCampaignOrganizations();

        List<CampaignOrganization.Referent> referentsModel = referents.stream()
                .map(referent -> new CampaignOrganization.Referent(
                        referent.firstName(),
                        referent.lastName(),
                        referent.phoneNumber(),
                        referent.role())).toList();

        List<CampaignOrganization.Interviewer> interviewersModel = interviewers.stream()
                .map(interviewer -> new CampaignOrganization.Interviewer(
                        interviewer.id(),
                        interviewer.label(),
                        interviewer.surveyUnitCount()
                )).toList();

        return new CampaignOrganization(
                campaign.id(),
                campaign.label(),
                campaign.identificationPhaseStartDate(),
                campaign.collectionStartDate(),
                campaign.collectionEndDate(),
                campaign.endDate(),
                CampaignPhase.fromDates(currentTimestamp,
                        campaign.managementStartDate(),
                        campaign.collectionStartDate(),
                        campaign.collectionEndDate(),
                        campaign.endDate()),
                referentsModel,
                interviewersModel,
                surveyUnits);
    }
}
