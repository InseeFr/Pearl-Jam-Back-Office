package fr.insee.pearljam.surveyunit.domain.service;

import fr.insee.pearljam.surveyunit.domain.model.Comment;
import fr.insee.pearljam.surveyunit.domain.model.CommentType;
import fr.insee.pearljam.surveyunit.domain.service.dummy.CommentFakeRepository;
import fr.insee.pearljam.surveyunit.domain.service.exception.SurveyUnitNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommentServiceImplTest {
    private CommentServiceImpl commentService;
    private CommentFakeRepository commentRepository;

    @BeforeEach
    void setup() {
        commentRepository = new CommentFakeRepository();
        commentService = new CommentServiceImpl(commentRepository);
    }

    @Test
    @DisplayName("Should update comment")
    void testUpdateComment01() throws SurveyUnitNotFoundException {
        Comment commentToUpdate = new Comment(CommentType.INTERVIEWER, "value", "11");
        commentService.updateSurveyUnitComment(commentToUpdate);
        assertThat(commentRepository.getCommentUpdated()).isEqualTo(commentToUpdate);
    }
}