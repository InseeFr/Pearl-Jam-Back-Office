package fr.insee.pearljam.surveyunit.domain.port.serverside;

import fr.insee.pearljam.surveyunit.domain.model.Comment;
import fr.insee.pearljam.surveyunit.domain.service.exception.SurveyUnitNotFoundException;

public interface CommentRepository {
    /**
     * Update comment of a survey unit
     * @param commentToUpdate comment
     * @throws SurveyUnitNotFoundException exception thrown when survey unit not found
     */
    void updateComment(Comment commentToUpdate) throws SurveyUnitNotFoundException;
}
