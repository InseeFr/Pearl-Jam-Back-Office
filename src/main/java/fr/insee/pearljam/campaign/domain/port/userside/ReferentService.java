package fr.insee.pearljam.campaign.domain.port.userside;

import java.util.List;

import fr.insee.pearljam.campaign.infrastructure.rest.dto.ReferentDto;

public interface ReferentService {

    List<ReferentDto> findByCampaignId(String id);
}
