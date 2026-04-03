package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.model.CampaignOrganization;
import fr.insee.pearljam.domain.campaign.port.in.CampaignOrganizationPort;
import fr.insee.pearljam.domain.campaign.port.in.CampaignVisibilityPort;
import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.campaign.port.out.CampaignReferentRepository;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignWithVisibility;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.Referent;
import fr.insee.pearljam.domain.reporting.readmodel.progress.CampaignPhase;
import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.stats.InterviewerDailyStats;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
@Service
@AllArgsConstructor
public class CampaignOrganizationService implements CampaignOrganizationPort {

    private final CampaignDailyStatsRepositoryPort campaignDailyStatsRepositoryPort;
    private final CampaignReferentRepository campaignReferentRepository;
    private final CampaignVisibilityPort campaignVisibilityPort;
    private final UserService userService;
    private final DateService dateService;
    private final Clock clock;

    @Override
    public CampaignOrganization getCampaignOrganizations(String userId, String campaignId) throws CampaignNotFoundException {
        LocalDate now = LocalDate.now(clock);
        List<String> userOUIds = getUserOUIds(userId);

        CampaignDailyStats campaignDailyStats = campaignDailyStatsRepositoryPort
                .findCampaignStats(campaignId, now)
                .orElseThrow(CampaignNotFoundException::new);

        CampaignWithVisibility campaign = campaignVisibilityPort.findCampaignVisibility(campaignId, userOUIds, userId);
        List<Referent> referents = campaignReferentRepository.getReferents(campaignId);
        List<InterviewerDailyStats> interviewerDailyStats = campaignDailyStatsRepositoryPort
                .getInterviewerStats(campaignId, userOUIds, now);

        return new CampaignOrganization(
                campaign.id(),
                campaign.label(),
                campaign.identificationPhaseStartDate(),
                campaign.collectionStartDate(),
                campaign.collectionEndDate(),
                campaign.endDate(),
                computePhase(campaign),
                toReferentModels(referents),
                toInterviewerModels(interviewerDailyStats),
                toSurveyUnitModel(campaignDailyStats));
    }

    private List<String> getUserOUIds(String userId) {
        return userService.getUserOUsModel(userId, true)
                .stream().map(OrganizationUnitSummary::getId).toList();
    }

    private CampaignPhase computePhase(CampaignWithVisibility campaign) {
        return CampaignPhase.fromDates(
                dateService.getCurrentTimestamp(),
                campaign.managementStartDate(),
                campaign.collectionStartDate(),
                campaign.collectionEndDate(),
                campaign.endDate());
    }

    private List<CampaignOrganization.Referent> toReferentModels(List<Referent> referents) {
        return referents.stream()
                .map(r -> new CampaignOrganization.Referent(
                        r.firstName(),
                        r.lastName(),
                        r.phoneNumber(),
                        r.role()))
                .toList();
    }

    private List<CampaignOrganization.Interviewer> toInterviewerModels(List<InterviewerDailyStats> stats) {
        return stats.stream()
                .map(i -> new CampaignOrganization.Interviewer(
                        i.getInterviewerId(),
                        i.getInterviewerFirstName() + " " + i.getInterviewerLastName(),
                        i.getAllocatedStateCount()))
                .toList();
    }

    private CampaignOrganization.CampaignOrganizationSurveyUnitCount toSurveyUnitModel(CampaignDailyStats stats) {
        return new CampaignOrganization.CampaignOrganizationSurveyUnitCount(
                stats.getAllocatedStateCount(),
                stats.getUnaffectedCount());
    }
}