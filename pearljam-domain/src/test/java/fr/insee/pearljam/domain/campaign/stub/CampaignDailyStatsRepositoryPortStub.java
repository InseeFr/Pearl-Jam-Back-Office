package fr.insee.pearljam.domain.campaign.stub;

import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerCampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.OrganizationUnitDailyStats;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class CampaignDailyStatsRepositoryPortStub implements CampaignDailyStatsRepositoryPort {

    private final List<CampaignDailyStats> campaignStats;
    private final List<InterviewerDailyStats> interviewerStats;


    public CampaignDailyStatsRepositoryPortStub(List<CampaignDailyStats> campaignStats, List<InterviewerDailyStats> interviewerStats) {
        this.campaignStats = campaignStats;
        this.interviewerStats = interviewerStats;
    }

    @Override
    public List<CampaignDailyStats> getCampaignsStats(List<String> campaignIds, List<String> ouIds, LocalDate day) {
        return campaignStats;
    }

    @Override
    public List<InterviewerCampaignDailyStats> getCampaignsStatsForInterviewer(String interviewerId, List<String> campaignIds, List<String> userOUIds, LocalDate day) {
        return List.of();
    }

    @Override
    public Optional<CampaignDailyStats> findCampaignStats(String campaignId, LocalDate day) {
        return campaignStats.stream()
                .filter(s -> campaignId.equals(s.getCampaignId()))
                .findFirst();
    }

    @Override
    public Optional<CampaignDailyStats> findCampaignStatsForOrganizationUnits(String campaignId, List<String> ouIds, LocalDate day) {
        return campaignStats.stream()
                .filter(s -> campaignId.equals(s.getCampaignId()))
                .findFirst();
    }

    @Override
    public List<OrganizationUnitDailyStats> getOrganizationUnitsStats(String campaignId, LocalDate day) {
        return List.of();
    }

    @Override
    public List<InterviewerDailyStats> getInterviewerStats(String campaignId, List<String> ouIds, LocalDate date) {
        return interviewerStats;
    }
}
