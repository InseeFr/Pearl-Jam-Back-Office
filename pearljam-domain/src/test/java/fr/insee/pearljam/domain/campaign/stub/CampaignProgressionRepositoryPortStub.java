package fr.insee.pearljam.domain.campaign.stub;

import fr.insee.pearljam.domain.reporting.port.out.CampaignProgressionRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignSummary;
import fr.insee.pearljam.domain.reporting.readmodel.CommunicationRequestCount;
import fr.insee.pearljam.domain.reporting.readmodel.StateCount;

import java.time.Instant;
import java.util.List;

public class CampaignProgressionRepositoryPortStub implements CampaignProgressionRepositoryPort {

    private final List<CampaignSummary> campaigns;
    private final List<StateCount> stateCounts;
    private final List<CommunicationRequestCount> commCounts;

    public CampaignProgressionRepositoryPortStub(
            List<CampaignSummary> campaigns,
            List<StateCount> stateCounts,
            List<CommunicationRequestCount> commCounts) {
        this.campaigns = campaigns;
        this.stateCounts = stateCounts;
        this.commCounts = commCounts;
    }

    @Override
    public List<CampaignSummary> getOpenedCampaignsByOrganisationUnits(List<String> orgUnitIds, Instant date) {
        return campaigns;
    }

    @Override
    public List<StateCount> getStateCountByCampaignsAndOrganisationUnits(
            List<String> campaignIds, List<String> orgUnitIds, Instant date) {
        return stateCounts;
    }

    @Override
    public List<CommunicationRequestCount> getComRequestCountsByCampaignsAndOrganisationUnits(
            List<String> campaignIds, List<String> orgUnitIds, Instant date) {
        return commCounts;
    }
}