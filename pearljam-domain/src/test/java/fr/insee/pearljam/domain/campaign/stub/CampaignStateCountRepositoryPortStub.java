package fr.insee.pearljam.domain.campaign.stub;

import fr.insee.pearljam.domain.reporting.port.out.CampaignStateCountRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.StateCount;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
public class CampaignStateCountRepositoryPortStub implements CampaignStateCountRepositoryPort {

    private final List<StateCount> stateCounts;

    @Override
    public List<StateCount> getStateCountByCampaignsAndOrganisationUnits(
            List<String> campaignIds, List<String> orgUnitIds, Instant date) {
        return stateCounts;
    }


}
