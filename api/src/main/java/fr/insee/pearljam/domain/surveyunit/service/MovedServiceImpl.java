package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.domain.surveyunit.port.serverside.MovedSurveyUnitRepository;
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
    private final MovedSurveyUnitRepository movedSurveyUnitRepository;

    @Override
    public void updateMovedSurveyUnit(String surveyUnitId) {
        movedSurveyUnitRepository.updateMovedSurveyUnit(surveyUnitId);
    }
}