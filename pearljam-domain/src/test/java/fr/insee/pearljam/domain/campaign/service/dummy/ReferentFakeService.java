package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.contracts.campaign.dto.ReferentDto;
import fr.insee.pearljam.domain.campaign.port.in.ReferentService;

import java.util.List;

public class ReferentFakeService implements ReferentService {

    @Override
    public List<ReferentDto> findByCampaignId(String id) {
        throw new UnsupportedOperationException("Unimplemented method 'findByCampaignId'");
    }
}
