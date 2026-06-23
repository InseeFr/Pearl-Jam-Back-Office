package fr.insee.pearljam.api.surveyunit.controller.dummy;

import fr.insee.pearljam.domain.surveyunit.model.Comment;
import fr.insee.pearljam.domain.surveyunit.port.in.CommentService;
import fr.insee.pearljam.domain.surveyunit.service.exception.SurveyUnitNotFoundException;
import lombok.Getter;
import lombok.Setter;

public class CommentFakeService implements CommentService {
    @Getter
    private Comment commentUpdated = null;

    @Setter
    private boolean shouldThrowException = false;

    @Override
    public void updateSurveyUnitComment(Comment comment) throws SurveyUnitNotFoundException {
        if(shouldThrowException) {
            throw new SurveyUnitNotFoundException(comment.surveyUnitId());
        }
        commentUpdated = comment;
    }
}
