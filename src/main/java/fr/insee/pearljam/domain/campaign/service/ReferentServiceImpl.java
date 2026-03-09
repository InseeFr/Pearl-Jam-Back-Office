package fr.insee.pearljam.domain.campaign.service;

import fr.insee.pearljam.api.campaign.dto.ReferentDto;
import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.campaign.port.out.ReferentRepository;
import fr.insee.pearljam.domain.campaign.port.in.ReferentService;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
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
