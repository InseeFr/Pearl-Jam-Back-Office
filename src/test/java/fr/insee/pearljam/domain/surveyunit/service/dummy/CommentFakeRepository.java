package fr.insee.pearljam.domain.surveyunit.service.dummy;

import fr.insee.pearljam.domain.surveyunit.model.Comment;
import fr.insee.pearljam.domain.surveyunit.port.serverside.CommentRepository;
import fr.insee.pearljam.domain.exception.SurveyUnitNotFoundException;
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
            throw new SurveyUnitNotFoundException();
        }
        commentUpdated = comment;
    }
}
