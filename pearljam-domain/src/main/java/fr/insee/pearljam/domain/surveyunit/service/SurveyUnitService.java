package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitPort;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SurveyUnitService implements SurveyUnitPort {

    private final SurveyUnitRepository surveyUnitRepository;

    @Override
    @Transactional
    public boolean existsSurveyUnitById(String surveyUnitId) {
        return surveyUnitRepository.existsById(surveyUnitId);
    }
}
