package fr.insee.pearljam.api.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.dto.referent.ReferentDto;
import fr.insee.pearljam.api.repository.ReferentRepository;
import fr.insee.pearljam.api.service.ReferentService;

@Service
public class ReferentServiceImpl implements ReferentService {

    @Autowired
    ReferentRepository referentRepository;

    @Override
    public List<ReferentDto> findByCampaignId(String id) {

        return referentRepository.findByCampaignId(id).stream().map(ref -> new ReferentDto(ref))
                .collect(Collectors.toList());
    }

}
