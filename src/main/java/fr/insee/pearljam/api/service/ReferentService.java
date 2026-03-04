package fr.insee.pearljam.api.service;

import java.util.List;

import fr.insee.pearljam.api.dto.referent.ReferentDto;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;

public interface ReferentService {

    List<ReferentDto> findByCampaignId(String id) throws CampaignNotFoundException;
}
