package fr.insee.pearljam.domain.reporting.port.out;

import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerCampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.OrganizationUnitDailyStats;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CampaignDailyStatsRepositoryPort {

    /**
     * Returns one {@link InterviewerDailyStats} per interviewer
     * for the given campaign, organization units and day snapshot.
     */
    List<InterviewerDailyStats> getInterviewerStats(String campaignId, List<String> ouIds, LocalDate day);

    /**
     * Returns one {@link OrganizationUnitDailyStats} per organization unit (with id and label)
     * for the given campaign and day snapshot.
     */
    List<OrganizationUnitDailyStats> getOrganizationUnitsStats(String campaignId, LocalDate day);

    /**
     * Returns the aggregated stats {@link CampaignDailyStats} for a campaign
     * across all given organization units for the given day snapshot.
     */
    Optional<CampaignDailyStats> findCampaignStatsForOrganizationUnits(String campaignId, List<String> ouIds, LocalDate day);

    /**
     * Returns the total stats {@link CampaignDailyStats}
     * for the entire campaign for the given day snapshot.
     */
    Optional<CampaignDailyStats> findCampaignStats(String campaignId, LocalDate day);

    /**
     * Returns one {@link CampaignDailyStats} per campaign (with id and label),
     * aggregated across the given organization units for the given day snapshot.
     */
    List<CampaignDailyStats> getCampaignsStats(List<String> campaignIds, List<String> ouIds, LocalDate day);

    /**
     * Returns one {@link InterviewerCampaignDailyStats} per campaign (with id and label),
     * aggregated across the given organization units for the given day snapshot for the given interviewer.
     */
    List<InterviewerCampaignDailyStats> getCampaignsStatsForInterviewer(String interviewerId,
                                                                        List<String> campaignIds,
                                                                        List<String> userOUIds, LocalDate day);
}
