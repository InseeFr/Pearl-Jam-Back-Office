package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.api.domain.*;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDetailDto;
import fr.insee.pearljam.api.service.impl.SurveyUnitUpdateServiceImpl;
import fr.insee.pearljam.api.surveyunit.dto.CommentDto;
import fr.insee.pearljam.api.surveyunit.dto.CommunicationRequestDto;
import fr.insee.pearljam.api.surveyunit.dto.CommunicationRequestStatusDto;
import fr.insee.pearljam.domain.surveyunit.model.CommentType;
import fr.insee.pearljam.domain.surveyunit.model.communication.*;
import fr.insee.pearljam.infrastructure.surveyunit.entity.CommentDB;
import fr.insee.pearljam.infrastructure.surveyunit.entity.CommunicationRequestDB;
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
    private SurveyUnitDetailDto surveyUnitDto;

    @BeforeEach
    void setup() {
        surveyUnitService = new SurveyUnitUpdateServiceImpl();
        surveyUnit = new SurveyUnit("id", true, true, null,
                null, null, null, null, null);
        Set<CommunicationRequestDB> communicationRequestDBs = new HashSet<>();
        communicationRequestDBs.add(new CommunicationRequestDB(10L, "mid", null, null, null, null, surveyUnit, null));
        surveyUnit.setCommunicationRequests(communicationRequestDBs);

        surveyUnitDto = new SurveyUnitDetailDto();
    }

    @Test
    @DisplayName("Should add communication requests for survey unit")
    void testUpdateCommunication01() {
        List<CommunicationRequestStatusDto> status1 = List.of(
                new CommunicationRequestStatusDto(null, 1233456789L, CommunicationStatusType.INITIATED),
                new CommunicationRequestStatusDto(2L, 123345678910L, CommunicationStatusType.FAILED)
        );

        List<CommunicationRequestStatusDto> status2 = List.of(
                new CommunicationRequestStatusDto(3L, 123345678911L, CommunicationStatusType.READY),
                new CommunicationRequestStatusDto(4L, 123345678912L, CommunicationStatusType.CANCELLED)
        );

        List<CommunicationRequestDto> communicationRequests = List.of(
                new CommunicationRequestDto(null, "messhugahid1", CommunicationRequestType.NOTICE,
                        CommunicationRequestReason.UNREACHABLE, CommunicationRequestMedium.EMAIL,
                        CommunicationRequestEmiter.INTERVIEWER, status1),
                new CommunicationRequestDto(null, "messhugahid2", CommunicationRequestType.REMINDER,
                        CommunicationRequestReason.REFUSAL, CommunicationRequestMedium.MAIL,
                        CommunicationRequestEmiter.TOOL, status2)
        );

        surveyUnitDto.setCommunicationRequests(communicationRequests);
        surveyUnitService.updateSurveyUnitInfos(surveyUnit, surveyUnitDto);

        Set<CommunicationRequestDB> communicationRequestResults = surveyUnit.getCommunicationRequests();
        assertThat(communicationRequestResults)
                .hasSize(3)
                .extracting(CommunicationRequestDB::getId,
                        CommunicationRequestDB::getMesshugahId,
                        CommunicationRequestDB::getType,
                        CommunicationRequestDB::getReason,
                        CommunicationRequestDB::getMedium,
                        CommunicationRequestDB::getEmiter,
                        CommunicationRequestDB::getSurveyUnit,
                        communicationRequestDB -> communicationRequestDB.getStatus() == null ? null : communicationRequestDB.getStatus().stream()
                                .map(status -> tuple(status.getId(), status.getDate(), status.getStatus()))
                                .toList()
                        )
                .containsExactlyInAnyOrder(
                        tuple(null, "messhugahid1", CommunicationRequestType.NOTICE,
                                CommunicationRequestReason.UNREACHABLE, CommunicationRequestMedium.EMAIL,
                                CommunicationRequestEmiter.INTERVIEWER, surveyUnit,
                                List.of(
                                        tuple(null, 1233456789L, CommunicationStatusType.INITIATED),
                                        tuple(2L, 123345678910L, CommunicationStatusType.FAILED)
                                )),
                        tuple(null, "messhugahid2", CommunicationRequestType.REMINDER,
                                CommunicationRequestReason.REFUSAL, CommunicationRequestMedium.MAIL,
                                CommunicationRequestEmiter.TOOL, surveyUnit,
                                List.of(
                                        tuple(3L, 123345678911L, CommunicationStatusType.READY),
                                        tuple(4L, 123345678912L, CommunicationStatusType.CANCELLED)
                                )),
                        tuple(10L, "mid", null, null, null, null, surveyUnit, null)
                );

        // Check that CommunicationRequestStatusDB has the correct parent
        communicationRequestResults.forEach(request -> {
            if (request.getStatus() != null) {
                request.getStatus().forEach(status -> assertThat(status.getCommunicationRequest()).isEqualTo(request));
            }
        });
    }

    @Test
    @DisplayName("Should not add existing requests (with id != null) for survey unit")
    void testUpdateCommunication02() {
        List<CommunicationRequestStatusDto> status = List.of(
                new CommunicationRequestStatusDto(null, 1233456789L, CommunicationStatusType.INITIATED),
                new CommunicationRequestStatusDto(2L, 123345678910L, CommunicationStatusType.FAILED)
        );

        List<CommunicationRequestDto> communicationRequests = List.of(
                new CommunicationRequestDto(1L, "messhugahid1", CommunicationRequestType.NOTICE,
                        CommunicationRequestReason.UNREACHABLE, CommunicationRequestMedium.EMAIL,
                        CommunicationRequestEmiter.INTERVIEWER, status),
                new CommunicationRequestDto(null, "messhugahid2", CommunicationRequestType.REMINDER,
                        CommunicationRequestReason.REFUSAL, CommunicationRequestMedium.MAIL,
                        CommunicationRequestEmiter.TOOL, null)
        );

        surveyUnitDto.setCommunicationRequests(communicationRequests);
        surveyUnitService.updateSurveyUnitInfos(surveyUnit, surveyUnitDto);

        Set<CommunicationRequestDB> communicationRequestResults = surveyUnit.getCommunicationRequests();
        assertThat(communicationRequestResults)
                .hasSize(2)
                .extracting(CommunicationRequestDB::getId)
                .containsExactlyInAnyOrder(10L, null);
    }

    @Test
    @DisplayName("Should add comments for survey unit")
    void testUpdateComments01() {
        List<CommentDto> comments = List.of(
                new CommentDto(CommentType.INTERVIEWER, "value1"),
                new CommentDto(CommentType.MANAGEMENT, "value2")
        );

        surveyUnitDto.setComments(comments);

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

        surveyUnitDto.setComments(comments);

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
}
