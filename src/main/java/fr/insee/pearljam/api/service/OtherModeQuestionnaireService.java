package fr.insee.pearljam.api.service;

import fr.insee.pearljam.api.dto.otherModeQuestionnaire.OtherModeQuestionnaireDto;
import org.springframework.http.HttpStatus;

public interface OtherModeQuestionnaireService {
    void addOtherModeQuestionnaire(OtherModeQuestionnaireDto otherModeQuestionnaire);
}
