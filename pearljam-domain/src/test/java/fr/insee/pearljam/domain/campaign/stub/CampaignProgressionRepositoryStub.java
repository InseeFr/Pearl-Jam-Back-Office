package fr.insee.pearljam.domain.campaign.stub;

import fr.insee.pearljam.domain.reporting.port.out.CampaignProgressionRepository;
import fr.insee.pearljam.domain.reporting.projection.CampaignProjection;
import fr.insee.pearljam.domain.reporting.projection.CommunicationRequestCountProjection;
import fr.insee.pearljam.domain.reporting.projection.StateCountProjection;

import java.util.List;

public class CampaignProgressionRepositoryStub implements CampaignProgressionRepository {

    private final List<CampaignProjection> campaigns;
    private final List<StateCountProjection> stateCounts;
    private final List<CommunicationRequestCountProjection> commCounts;

    public CampaignProgressionRepositoryStub(
            List<CampaignProjection> campaigns,
            List<StateCountProjection> stateCounts,
            List<CommunicationRequestCountProjection> commCounts) {
        this.campaigns = campaigns;
        this.stateCounts = stateCounts;
        this.commCounts = commCounts;
    }

    @Override
    public List<CampaignProjection> getCampaignsByOrganisationUnits(List<String> orgUnitIds) {
        return campaigns;
    }

    @Override
    public List<StateCountProjection> getStateCountByCampaignsAndOrganisationUnits(
            List<String> campaignIds, List<String> orgUnitIds, Long date) {
        return stateCounts;
    }

    @Override
    public List<CommunicationRequestCountProjection> getComRequestCountsByCampaignsAndOrganisationUnits(
            List<String> campaignIds, List<String> orgUnitIds, Long date) {
        return commCounts;
    }
}