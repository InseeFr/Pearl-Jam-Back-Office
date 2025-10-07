package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.domain.surveyunit.port.userside.MovedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MovedServiceImpl implements MovedService {
    private final SurveyUnitRepository surveyUnitRepository;

    @Override
    public void updateMovedSurveyUnit(String surveyUnitId) {
        var surveyUnit = surveyUnitRepository.findById(surveyUnitId);
        surveyUnit.ifPresent(unit -> {
            unit.setPriority(true);
            surveyUnitRepository.save(unit);
        });
    }
}
