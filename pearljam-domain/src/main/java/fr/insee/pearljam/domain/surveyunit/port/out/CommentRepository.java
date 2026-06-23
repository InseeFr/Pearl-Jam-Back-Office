package fr.insee.pearljam.domain.surveyunit.port.out;

import fr.insee.pearljam.domain.surveyunit.model.Comment;
import fr.insee.pearljam.domain.surveyunit.service.exception.SurveyUnitNotFoundException;

public interface CommentRepository {
    /**
     * Update comment of a survey unit
     * @param commentToUpdate comment
     * @throws SurveyUnitNotFoundException exception thrown when survey unit not found
     */
    void updateComment(Comment commentToUpdate) throws SurveyUnitNotFoundException;
}
