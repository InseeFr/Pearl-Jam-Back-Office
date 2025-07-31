package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.api.dto.otherModeQuestionnaire.OtherModeQuestionnaireDto;
import fr.insee.pearljam.api.service.OtherModeQuestionnaireService;
import fr.insee.pearljam.domain.surveyunit.port.state.StatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {
    private final OtherModeQuestionnaireService otherModeQuestionnaireService;

    @Override
    public void updateStatus(String surveyUnit, String type) {
        otherModeQuestionnaireService.addOtherModeQuestionnaire(new OtherModeQuestionnaireDto(surveyUnit, type));
    }
}
