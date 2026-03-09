package fr.insee.pearljam.domain.campaign.port.userside;

import java.util.List;

import fr.insee.pearljam.api.campaign.dto.ReferentDto;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;

public interface ReferentService {

    List<ReferentDto> findByCampaignId(String id) throws CampaignNotFoundException;
}
