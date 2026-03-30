package fr.insee.pearljam.domain.campaign.stub;

import fr.insee.pearljam.domain.reporting.port.out.CampaignProgressionRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignSummary;
import fr.insee.pearljam.domain.reporting.readmodel.CommunicationRequestCount;

import java.time.Instant;
import java.util.List;

public class CampaignProgressionRepositoryPortStub implements CampaignProgressionRepositoryPort {

    private final List<CampaignSummary> campaigns;
    private final List<CommunicationRequestCount> commCounts;

    public CampaignProgressionRepositoryPortStub(
            List<CampaignSummary> campaigns,
            List<CommunicationRequestCount> commCounts) {
        this.campaigns = campaigns;
        this.commCounts = commCounts;
    }

    @Override
    public List<CampaignSummary> getAllManagedAndNotClosedCampaignsByOrganisationUnits(List<String> orgUnitIds, Instant date) {
        return campaigns;
    }


    @Override
    public List<CommunicationRequestCount> getComRequestCountsByCampaignsAndOrganisationUnits(
            List<String> campaignIds, List<String> orgUnitIds, Instant date) {
        return commCounts;
    }
}