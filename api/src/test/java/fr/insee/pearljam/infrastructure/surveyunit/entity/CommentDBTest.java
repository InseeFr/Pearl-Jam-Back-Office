package fr.insee.pearljam.infrastructure.surveyunit.entity;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.domain.surveyunit.model.Comment;
import fr.insee.pearljam.domain.surveyunit.model.CommentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommentDBTest {

    private SurveyUnit surveyUnitDB;

    @BeforeEach
    void setup() {
        surveyUnitDB = new SurveyUnit();
        surveyUnitDB.setId("su-id");
    }

    @Test
    @DisplayName("Should return model object")
    void testToModel01() {
        CommentDB commentDB = new CommentDB(1L, CommentType.INTERVIEWER, "value", surveyUnitDB);

        Comment comment = CommentDB.toModel(commentDB);
        assertThat(comment.type()).isEqualTo(commentDB.getType());
        assertThat(comment.value()).isEqualTo(commentDB.getValue());
        assertThat(comment.surveyUnitId()).isEqualTo(surveyUnitDB.getId());
    }

    @Test
    @DisplayName("Should return entity object")
    void testFromModel01() {
        Comment comment = new Comment(CommentType.INTERVIEWER, "value", "id-su");

        CommentDB commentDB = CommentDB.fromModel(surveyUnitDB, comment);
        assertThat(commentDB.getType()).isEqualTo(comment.type());
        assertThat(commentDB.getValue()).isEqualTo(comment.value());
        assertThat(commentDB.getSurveyUnit()).isEqualTo(surveyUnitDB);
        assertThat(commentDB.getId()).isNull();
    }
}
