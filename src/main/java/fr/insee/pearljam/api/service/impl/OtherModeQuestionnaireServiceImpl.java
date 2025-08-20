package fr.insee.pearljam.api.service.impl;

import fr.insee.pearljam.api.domain.OtherModeQuestionnaireState;
import fr.insee.pearljam.api.dto.otherModeQuestionnaire.OtherModeQuestionnaireDto;
import fr.insee.pearljam.api.repository.OtherModeQuestionnaireRepository;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.domain.surveyunit.model.OtherModeQuestionnaire;
import fr.insee.pearljam.domain.surveyunit.port.serverside.OtherModeQuestionnaireService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class OtherModeQuestionnaireServiceImpl implements OtherModeQuestionnaireService {

    private final OtherModeQuestionnaireRepository repository;
    private final SurveyUnitRepository surveyUnitRepository;


    @Override
    public void addOtherModeQuestionnaire(OtherModeQuestionnaire otherModeQuestionnaire) {
        var surveyUnit = surveyUnitRepository.findById(otherModeQuestionnaire.surveyUnitId());
        var otherModeQuestionnaireState = new OtherModeQuestionnaireState();
        otherModeQuestionnaireState.setState(otherModeQuestionnaire.type());
        otherModeQuestionnaireState.setSurveyUnit(surveyUnit.get());
        this.repository.save(otherModeQuestionnaireState);
    }
}
