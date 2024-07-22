package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.api.domain.*;
import fr.insee.pearljam.api.service.impl.SurveyUnitUpdateServiceImpl;
import fr.insee.pearljam.api.surveyunit.dto.*;
import fr.insee.pearljam.domain.surveyunit.model.CommentType;
import fr.insee.pearljam.domain.surveyunit.model.communication.*;
import fr.insee.pearljam.domain.surveyunit.model.question.*;
import fr.insee.pearljam.infrastructure.surveyunit.entity.CommentDB;
import fr.insee.pearljam.infrastructure.surveyunit.entity.CommunicationRequestDB;
import fr.insee.pearljam.infrastructure.surveyunit.entity.IdentificationDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class SurveyUnitUpdateServiceImplTest {
    private SurveyUnitUpdateServiceImpl surveyUnitService;
    private SurveyUnit surveyUnit;
    private SurveyUnitUpdateDto surveyUnitDto;

    @BeforeEach
    void setup() {
        surveyUnitService = new SurveyUnitUpdateServiceImpl();
        surveyUnit = new SurveyUnit("id", true, true, null,
                null, null, null, null, null);
        Set<CommunicationRequestDB> communicationRequestDBs = new HashSet<>();
        communicationRequestDBs.add(new CommunicationRequestDB(10L, 3L,
                CommunicationRequestReason.REFUSAL,
                CommunicationRequestEmitter.TOOL,
                surveyUnit, null));
        surveyUnit.setCommunicationRequests(communicationRequestDBs);
    }

    @Test
    @DisplayName("Should add communication requests for survey unit")
    void testUpdateCommunication01() {
        List<CommunicationRequestCreateDto> communicationRequests = List.of(
                new CommunicationRequestCreateDto(1L, 12345678910L,
                        CommunicationRequestReason.UNREACHABLE),
                new CommunicationRequestCreateDto(2L, 1234567891011L,
                        CommunicationRequestReason.REFUSAL)
        );

        surveyUnitDto = createSurveyUnitDto(null, null, communicationRequests);
        surveyUnitService.updateSurveyUnitInfos(surveyUnit, surveyUnitDto);

        Set<CommunicationRequestDB> communicationRequestResults = surveyUnit.getCommunicationRequests();
        assertThat(communicationRequestResults)
                .hasSize(3)
                .extracting(CommunicationRequestDB::getId,
                        CommunicationRequestDB::getCommunicationTemplateId,
                        CommunicationRequestDB::getReason,
                        CommunicationRequestDB::getEmitter,
                        CommunicationRequestDB::getSurveyUnit,
                        communicationRequestDB -> communicationRequestDB.getStatus() == null ? null : communicationRequestDB.getStatus().stream()
                                .map(status -> tuple(status.getId(), status.getDate(), status.getStatus()))
                                .toList()
                        )
                .containsExactlyInAnyOrder(
                        tuple(null,
                                1L,
                                CommunicationRequestReason.UNREACHABLE,
                                CommunicationRequestEmitter.INTERVIEWER,
                                surveyUnit,
                                List.of(tuple(null, 12345678910L, CommunicationStatusType.INITIATED))
                        ),
                        tuple(null,
                                2L,
                                CommunicationRequestReason.REFUSAL,
                                CommunicationRequestEmitter.INTERVIEWER,
                                surveyUnit,
                                List.of(tuple(null, 1234567891011L, CommunicationStatusType.INITIATED))
                        ),
                        tuple(10L,
                                3L,
                                CommunicationRequestReason.REFUSAL,
                                CommunicationRequestEmitter.TOOL,
                                surveyUnit,
                                null)
                );

        // Check that CommunicationRequestStatusDB has the correct parent
        communicationRequestResults.forEach(request -> {
            if (request.getStatus() != null) {
                request.getStatus().forEach(status -> assertThat(status.getCommunicationRequest()).isEqualTo(request));
            }
        });
    }

    @Test
    @DisplayName("Should add comments for survey unit")
    void testUpdateComments01() {
        List<CommentDto> comments = List.of(
                new CommentDto(CommentType.INTERVIEWER, "value1"),
                new CommentDto(CommentType.MANAGEMENT, "value2")
        );

        surveyUnitDto = createSurveyUnitDto(null, comments, null);

        surveyUnitService.updateSurveyUnitInfos(surveyUnit, surveyUnitDto);
        assertThat(surveyUnit.getComments())
                .hasSize(2)
                .extracting(CommentDB::getId,
                        CommentDB::getType,
                        CommentDB::getValue,
                        CommentDB::getSurveyUnit
                ).contains(
                        tuple(null, CommentType.INTERVIEWER, "value1", surveyUnit),
                        tuple(null, CommentType.MANAGEMENT, "value2", surveyUnit)
                );
    }

    @Test
    @DisplayName("Should remove survey unit comments with same type of inputted comments")
    void testUpdateComments02() {
        surveyUnit.getComments().addAll(Set.of(
                new CommentDB(1L, CommentType.INTERVIEWER, "value1", surveyUnit),
                new CommentDB(2L, CommentType.INTERVIEWER, "value2", surveyUnit),
                new CommentDB(3L, CommentType.MANAGEMENT, "value3", surveyUnit)
        ));

        List<CommentDto> comments = List.of(
                new CommentDto(CommentType.INTERVIEWER, "value4")
        );

        surveyUnitDto = createSurveyUnitDto(null, comments, null);

        surveyUnitService.updateSurveyUnitInfos(surveyUnit, surveyUnitDto);
        assertThat(surveyUnit.getComments())
                .hasSize(2)
                .extracting(CommentDB::getId,
                        CommentDB::getType,
                        CommentDB::getValue,
                        CommentDB::getSurveyUnit
                ).contains(
                        tuple(null, CommentType.INTERVIEWER, "value4", surveyUnit),
                        tuple(3L, CommentType.MANAGEMENT, "value3", surveyUnit)
                );
    }

    @Test
    @DisplayName("Should update identification")
    void testUpdateIdentification01() {
        IdentificationDB identificationDB = new IdentificationDB(2L,
                IdentificationQuestionValue.IDENTIFIED,
                AccessQuestionValue.ACC,
                SituationQuestionValue.ORDINARY,
                CategoryQuestionValue.SECONDARY,
                OccupantQuestionValue.IDENTIFIED,
                surveyUnit);
        surveyUnit.setIdentification(identificationDB);

        IdentificationDto identification = new IdentificationDto(IdentificationQuestionValue.UNIDENTIFIED,
                AccessQuestionValue.NACC,
                SituationQuestionValue.NOORDINARY,
                CategoryQuestionValue.VACANT,
                OccupantQuestionValue.UNIDENTIFIED);
        surveyUnitDto = createSurveyUnitDto(identification, null, null);

        surveyUnitService.updateSurveyUnitInfos(surveyUnit, surveyUnitDto);
        IdentificationDB identificationResult = surveyUnit.getIdentification();
        assertThat(identificationResult.getId()).isEqualTo(2L);
        assertThat(identificationResult.getIdentification()).isEqualTo(IdentificationQuestionValue.UNIDENTIFIED);
        assertThat(identificationResult.getAccess()).isEqualTo(AccessQuestionValue.NACC);
        assertThat(identificationResult.getSituation()).isEqualTo(SituationQuestionValue.NOORDINARY);
        assertThat(identificationResult.getCategory()).isEqualTo(CategoryQuestionValue.VACANT);
        assertThat(identificationResult.getOccupant()).isEqualTo(OccupantQuestionValue.UNIDENTIFIED);
        assertThat(identificationResult.getSurveyUnit()).isEqualTo(surveyUnit);
    }

    @Test
    @DisplayName("Should not update identification entity when identification model is null")
    void testUpdateIdentification02() {
        IdentificationDB identificationDB = new IdentificationDB(2L,
                IdentificationQuestionValue.IDENTIFIED,
                AccessQuestionValue.ACC,
                SituationQuestionValue.ABSORBED,
                CategoryQuestionValue.SECONDARY,
                OccupantQuestionValue.IDENTIFIED,
                surveyUnit);
        surveyUnit.setIdentification(identificationDB);
        surveyUnitDto = createSurveyUnitDto(null, null, null);
        surveyUnitService.updateSurveyUnitInfos(surveyUnit, surveyUnitDto);
        assertThat(surveyUnit.getIdentification()).isEqualTo(identificationDB);
    }

    @Test
    @DisplayName("Should create identification entity when entity does not exist")
    void testUpdateIdentification03() {
        surveyUnit.setIdentification(null);
        IdentificationDto identification = new IdentificationDto(IdentificationQuestionValue.UNIDENTIFIED,
                AccessQuestionValue.NACC,
                SituationQuestionValue.NOORDINARY,
                CategoryQuestionValue.VACANT,
                OccupantQuestionValue.UNIDENTIFIED);
        surveyUnitDto = createSurveyUnitDto(identification, null, null);

        surveyUnitService.updateSurveyUnitInfos(surveyUnit, surveyUnitDto);

        IdentificationDB identificationResult = surveyUnit.getIdentification();
        assertThat(identificationResult.getId()).isNull();
        assertThat(identificationResult.getIdentification()).isEqualTo(IdentificationQuestionValue.UNIDENTIFIED);
        assertThat(identificationResult.getAccess()).isEqualTo(AccessQuestionValue.NACC);
        assertThat(identificationResult.getSituation()).isEqualTo(SituationQuestionValue.NOORDINARY);
        assertThat(identificationResult.getCategory()).isEqualTo(CategoryQuestionValue.VACANT);
        assertThat(identificationResult.getOccupant()).isEqualTo(OccupantQuestionValue.UNIDENTIFIED);
        assertThat(identificationResult.getSurveyUnit()).isEqualTo(surveyUnit);
    }

    private SurveyUnitUpdateDto createSurveyUnitDto(IdentificationDto identification, List<CommentDto> comments, List<CommunicationRequestCreateDto> communicationRequests) {
        return new SurveyUnitUpdateDto("su-id", null, null, true,
                comments, null, null, null, identification, communicationRequests);
    }
}
