package fr.insee.pearljam.surveyunit.domain.service.dummy;

import fr.insee.pearljam.surveyunit.domain.model.Comment;
import fr.insee.pearljam.surveyunit.domain.port.serverside.CommentRepository;
import fr.insee.pearljam.surveyunit.domain.service.exception.SurveyUnitNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class CommentFakeRepository implements CommentRepository {

    @Getter
    private Comment commentUpdated = null;

    @Setter
    private boolean shouldThrowException = false;

    @Override
    public void updateComment(Comment comment) throws SurveyUnitNotFoundException {
        if(shouldThrowException) {
            throw new SurveyUnitNotFoundException(comment.surveyUnitId());
        }
        commentUpdated = comment;
    }
}
