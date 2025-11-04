package fr.insee.pearljam.campaign.infrastructure.rest.controller.dummy;

import java.util.List;

import fr.insee.pearljam.campaign.infrastructure.rest.dto.ReferentDto;
import fr.insee.pearljam.campaign.domain.port.userside.ReferentService;

public class ReferentFakeService implements ReferentService {

    @Override
    public List<ReferentDto> findByCampaignId(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByCampaignId'");
    }

}
