package fr.insee.pearljam.api.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.dto.referent.ReferentDto;
import fr.insee.pearljam.api.repository.ReferentRepository;
import fr.insee.pearljam.api.service.ReferentService;
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
