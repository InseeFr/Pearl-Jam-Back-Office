package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitExistencePort;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SurveyUnitExistenceService implements SurveyUnitExistencePort {

    private final SurveyUnitRepository surveyUnitRepository;

    @Override
    @Transactional
    public List<String> findExistingIds(List<String> surveyUnitIds) {
        return surveyUnitRepository.findExistingIds(surveyUnitIds);
    }


}
