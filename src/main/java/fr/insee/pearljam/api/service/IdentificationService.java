package fr.insee.pearljam.api.service;

import fr.insee.pearljam.api.domain.Identification;
import org.springframework.stereotype.Service;

@Service
public interface IdentificationService {

    public Identification findBySurveyUnitId(String id);
}
