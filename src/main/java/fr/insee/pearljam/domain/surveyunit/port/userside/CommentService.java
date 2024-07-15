package fr.insee.pearljam.domain.surveyunit.port.userside;

import fr.insee.pearljam.domain.surveyunit.model.Comment;
import fr.insee.pearljam.domain.exception.SurveyUnitNotFoundException;

public interface CommentService {
    /**
     * Update comment of a survey unit
     * @param comment comment
     * @throws SurveyUnitNotFoundException exception thrown when survey unit not found
     */
    void updateSurveyUnitComment(Comment comment) throws SurveyUnitNotFoundException;
}

