package fr.insee.pearljam.api.campaign.controller.dummy;

import java.util.List;

import fr.insee.pearljam.contracts.campaign.dto.ReferentDto;
import fr.insee.pearljam.domain.campaign.port.in.ReferentService;

public class ReferentFakeService implements ReferentService {

    @Override
    public List<ReferentDto> findByCampaignId(String id) {
        throw new UnsupportedOperationException("Unimplemented method 'findByCampaignId'");
    }

}
