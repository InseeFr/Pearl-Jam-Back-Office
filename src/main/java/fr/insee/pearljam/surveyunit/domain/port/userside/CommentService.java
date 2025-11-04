package fr.insee.pearljam.surveyunit.domain.port.userside;

import fr.insee.pearljam.surveyunit.domain.model.Comment;
import fr.insee.pearljam.surveyunit.domain.service.exception.SurveyUnitNotFoundException;

public interface CommentService {
    /**
     * Update comment of a survey unit
     * @param comment comment
     * @throws SurveyUnitNotFoundException exception thrown when survey unit not found
     */
    void updateSurveyUnitComment(Comment comment) throws SurveyUnitNotFoundException;
}

