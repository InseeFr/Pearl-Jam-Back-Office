package fr.insee.pearljam.surveyunit.infrastructure.rest.controller.dummy;

import fr.insee.pearljam.surveyunit.domain.model.Comment;
import fr.insee.pearljam.surveyunit.domain.port.userside.CommentService;
import fr.insee.pearljam.surveyunit.domain.service.exception.SurveyUnitNotFoundException;
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
