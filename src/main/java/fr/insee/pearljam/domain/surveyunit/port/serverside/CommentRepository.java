package fr.insee.pearljam.domain.surveyunit.port.serverside;

import fr.insee.pearljam.domain.surveyunit.model.Comment;
import fr.insee.pearljam.domain.exception.SurveyUnitNotFoundException;

public interface CommentRepository {
    /**
     * Update comment of a survey unit
     * @param commentToUpdate comment
     * @throws SurveyUnitNotFoundException exception thrown when survey unit not found
     */
    void updateComment(Comment commentToUpdate) throws SurveyUnitNotFoundException;
}
