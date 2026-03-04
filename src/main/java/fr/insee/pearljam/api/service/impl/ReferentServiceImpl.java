package fr.insee.pearljam.api.service.impl;

import fr.insee.pearljam.api.dto.referent.ReferentDto;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.service.ReferentService;
import fr.insee.pearljam.domain.campaign.port.serverside.ReferentRepository;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReferentServiceImpl implements ReferentService {

    private final ReferentRepository referentRepository;
    private final CampaignRepository campaignRepository;

    @Override
    public List<ReferentDto> findByCampaignId(String id) throws CampaignNotFoundException {
        if (!campaignRepository.existsById(id)) {
            throw new CampaignNotFoundException();
        }
        return referentRepository.findByCampaignId(id).stream().map(ReferentDto::new)
                .collect(Collectors.toList());
    }

}
