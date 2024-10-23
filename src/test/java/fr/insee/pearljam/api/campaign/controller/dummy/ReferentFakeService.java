package fr.insee.pearljam.api.campaign.controller.dummy;

import java.util.List;

import fr.insee.pearljam.api.dto.referent.ReferentDto;
import fr.insee.pearljam.api.service.ReferentService;

public class ReferentFakeService implements ReferentService {

    @Override
    public List<ReferentDto> findByCampaignId(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByCampaignId'");
    }

}
