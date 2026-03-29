package fr.insee.pearljam.domain.campaign.stub;

import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.stats.InterviewerDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.stats.OrganizationUnitDailyStats;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class CampaignDailyStatsRepositoryPortStub implements CampaignDailyStatsRepositoryPort {

    private final List<CampaignDailyStats> campaignStats;

    public CampaignDailyStatsRepositoryPortStub(List<CampaignDailyStats> campaignStats) {
        this.campaignStats = campaignStats;
    }

    @Override
    public List<CampaignDailyStats> getCampaignsStats(List<String> campaignIds, List<String> ouIds, LocalDate day) {
        return campaignStats;
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
    public List<OrganizationUnitDailyStats> getOrganizationUnitsStats(String campaignId, List<String> ouIds, LocalDate day) {
        return List.of();
    }

    @Override
    public List<InterviewerDailyStats> getInterviewerStats(String campaignId, List<String> ouIds, LocalDate day) {
        return List.of();
    }
}
