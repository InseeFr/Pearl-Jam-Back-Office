package fr.insee.pearljam.campaign.domain.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import fr.insee.pearljam.campaign.infrastructure.rest.dto.ReferentDto;
import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.repository.ReferentRepository;
import fr.insee.pearljam.campaign.domain.port.userside.ReferentService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReferentServiceImpl implements ReferentService {

    private final ReferentRepository referentRepository;

    @Override
    public List<ReferentDto> findByCampaignId(String id) {

        return referentRepository.findByCampaignId(id).stream().map(ReferentDto::new)
                .collect(Collectors.toList());
    }

}
