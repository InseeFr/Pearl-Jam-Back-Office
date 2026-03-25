package fr.insee.pearljam.domain.campaign.stub;

import fr.insee.pearljam.domain.reporting.port.out.CampaignProgressionRepository;
import fr.insee.pearljam.domain.reporting.query.CampaignQueryResponse;
import fr.insee.pearljam.domain.reporting.query.CommunicationRequestCountQueryResponse;
import fr.insee.pearljam.domain.reporting.query.StateCountQueryResponse;

import java.util.List;

public class CampaignProgressionRepositoryStub implements CampaignProgressionRepository {

    private final List<CampaignQueryResponse> campaigns;
    private final List<StateCountQueryResponse> stateCounts;
    private final List<CommunicationRequestCountQueryResponse> commCounts;

    public CampaignProgressionRepositoryStub(
            List<CampaignQueryResponse> campaigns,
            List<StateCountQueryResponse> stateCounts,
            List<CommunicationRequestCountQueryResponse> commCounts) {
        this.campaigns = campaigns;
        this.stateCounts = stateCounts;
        this.commCounts = commCounts;
    }

    @Override
    public List<CampaignQueryResponse> getOpenedCampaignsByOrganisationUnits(List<String> orgUnitIds, Long date) {
        return campaigns;
    }

    @Override
    public List<StateCountQueryResponse> getStateCountByCampaignsAndOrganisationUnits(
            List<String> campaignIds, List<String> orgUnitIds, Long date) {
        return stateCounts;
    }

    @Override
    public List<CommunicationRequestCountQueryResponse> getComRequestCountsByCampaignsAndOrganisationUnits(
            List<String> campaignIds, List<String> orgUnitIds, Long date) {
        return commCounts;
    }
}