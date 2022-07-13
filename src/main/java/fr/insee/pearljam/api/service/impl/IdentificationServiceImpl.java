package fr.insee.pearljam.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.domain.Identification;
import fr.insee.pearljam.api.repository.IdentificationRepository;
import fr.insee.pearljam.api.service.IdentificationService;

@Service
public class IdentificationServiceImpl implements IdentificationService {

    @Autowired
    IdentificationRepository identificationRepository;

    @Override
    public Identification findBySurveyUnitId(String id) {
        return identificationRepository.findBySurveyUnitId(id);
    }

}
