package fr.insee.pearljam.infrastructure.surveyunit.adapter;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.domain.surveyunit.model.Comment;
import fr.insee.pearljam.domain.surveyunit.model.CommentType;
import fr.insee.pearljam.domain.exception.SurveyUnitNotFoundException;
import fr.insee.pearljam.infrastructure.surveyunit.entity.CommentDB;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("auth")
@Transactional
@Slf4j
class CommentDaoAdapterTest {

    @Autowired
    private CommentDaoAdapter commentDaoAdapter;

    @Autowired
    private SurveyUnitRepository surveyUnitRepository;

    private CommentDB commentDB1, commentDB2, commentDB3;

    private final String surveyUnitId = "SU2";

    @BeforeEach
    void setup() {
        SurveyUnit surveyUnit = new SurveyUnit();
        surveyUnit.setId(surveyUnitId);
        commentDB1 = new CommentDB(null, CommentType.INTERVIEWER, "value1", surveyUnit);
        commentDB2 = new CommentDB(null, CommentType.MANAGEMENT, "value2", surveyUnit);
        commentDB3 = new CommentDB(null, CommentType.INTERVIEWER, "value3", surveyUnit);
        Set<CommentDB> comments = Set.of(commentDB1, commentDB2, commentDB3);
        surveyUnit.getComments().addAll(comments);
        surveyUnitRepository.save(surveyUnit);
    }

    @Test
    @DisplayName("Should update comment object")
    void testUpdateComment01() throws SurveyUnitNotFoundException {
        Comment comment = new Comment(CommentType.INTERVIEWER, "value4", surveyUnitId);
        commentDaoAdapter.updateComment(comment);
        Optional<SurveyUnit> surveyUnitOptional = surveyUnitRepository.findById(surveyUnitId);
        assertThat(surveyUnitOptional).isPresent();
        SurveyUnit surveyUnit = surveyUnitOptional.get();
        Set<CommentDB> comments = surveyUnit.getComments();

        assertThat(comments)
                .hasSize(2)
                .anySatisfy(commentUpdated -> {
                    assertThat(commentUpdated.getType()).isEqualTo(CommentType.INTERVIEWER);
                    assertThat(commentUpdated.getValue()).isEqualTo("value4");
                    assertThat(commentUpdated.getSurveyUnit()).isEqualTo(surveyUnit);
                    assertThat(commentUpdated.getId()).isNotNull();
                })
                .anySatisfy(commentUpdated -> {
                    assertThat(commentUpdated.getType()).isEqualTo(CommentType.MANAGEMENT);
                    assertThat(commentUpdated.getValue()).isEqualTo("value2");
                    assertThat(commentUpdated.getSurveyUnit()).isEqualTo(surveyUnit);
                    assertThat(commentUpdated.getId()).isNotNull();
                });

    }

    @Test
    @DisplayName("Should throw exception when survey unit doesn't exist")
    void testUpdateComment02() {
        String invalidSurveyUnitId = "invalid-id";
        Comment comment = new Comment(CommentType.INTERVIEWER, "value4", invalidSurveyUnitId);
        assertThatThrownBy(() -> commentDaoAdapter.updateComment(comment))
                .isInstanceOf(SurveyUnitNotFoundException.class)
                .hasMessage(String.format(SurveyUnitNotFoundException.MESSAGE, invalidSurveyUnitId));
    }
}

