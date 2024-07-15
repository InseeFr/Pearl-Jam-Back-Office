package fr.insee.pearljam.infrastructure.surveyunit.entity;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.domain.surveyunit.model.communication.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class CommunicationRequestDBTest {

    private SurveyUnit surveyUnit;

    @BeforeEach
    void setup() {
        surveyUnit = new SurveyUnit();
        surveyUnit.setId("su-id");
    }

    @Test
    @DisplayName("Should return model object")
    void testToModel01() {
        List<CommunicationRequestStatusDB> statusDB = List.of(
                new CommunicationRequestStatusDB(null, 1233456789L, CommunicationStatusType.INITIATED, null),
                new CommunicationRequestStatusDB(2L, 123345678910L, CommunicationStatusType.FAILED, null)
        );
        CommunicationRequestDB communicationRequestDB = new CommunicationRequestDB(1L, "messhugahid1", CommunicationRequestType.NOTICE,
                CommunicationRequestReason.UNREACHABLE, CommunicationRequestMedium.EMAIL,
                CommunicationRequestEmiter.INTERVIEWER, surveyUnit, statusDB);

        CommunicationRequest communicationRequest = CommunicationRequestDB.toModel(communicationRequestDB);
        assertThat(communicationRequest.id()).isEqualTo(communicationRequestDB.getId());
        assertThat(communicationRequest.messhugahId()).isEqualTo(communicationRequestDB.getMesshugahId());
        assertThat(communicationRequest.type()).isEqualTo(communicationRequestDB.getType());
        assertThat(communicationRequest.emiter()).isEqualTo(communicationRequestDB.getEmiter());
        assertThat(communicationRequest.medium()).isEqualTo(communicationRequestDB.getMedium());
        assertThat(communicationRequest.reason()).isEqualTo(communicationRequestDB.getReason());
        List<CommunicationRequestStatus> status = statusDB.stream()
                .map(CommunicationRequestStatusDB::toModel)
                .toList();
        assertThat(communicationRequest.status()).containsExactlyInAnyOrderElementsOf(status);
    }

    @Test
    @DisplayName("Should return entity object")
    void testFromModel01() {
        List<CommunicationRequestStatus> status = List.of(
                new CommunicationRequestStatus(null, 1233456789L, CommunicationStatusType.INITIATED),
                new CommunicationRequestStatus(2L, 123345678910L, CommunicationStatusType.FAILED)
        );

        CommunicationRequest communicationRequest = new CommunicationRequest(1L, "messhugahid1", CommunicationRequestType.NOTICE,
                CommunicationRequestReason.UNREACHABLE, CommunicationRequestMedium.EMAIL,
                CommunicationRequestEmiter.INTERVIEWER, status);

        CommunicationRequestDB communicationRequestDB = CommunicationRequestDB.fromModel(communicationRequest, surveyUnit);
        assertThat(communicationRequestDB.getId()).isEqualTo(communicationRequest.id());
        assertThat(communicationRequestDB.getMesshugahId()).isEqualTo(communicationRequest.messhugahId());
        assertThat(communicationRequestDB.getType()).isEqualTo(communicationRequest.type());
        assertThat(communicationRequestDB.getEmiter()).isEqualTo(communicationRequest.emiter());
        assertThat(communicationRequestDB.getMedium()).isEqualTo(communicationRequest.medium());
        assertThat(communicationRequestDB.getReason()).isEqualTo(communicationRequest.reason());
        assertThat(communicationRequestDB.getStatus())
                .extracting(CommunicationRequestStatusDB::getId,
                        CommunicationRequestStatusDB::getDate,
                        CommunicationRequestStatusDB::getStatus,
                        CommunicationRequestStatusDB::getCommunicationRequest)
                .containsExactlyInAnyOrder(
                        tuple(null, 1233456789L, CommunicationStatusType.INITIATED, communicationRequestDB),
                        tuple(2L, 123345678910L, CommunicationStatusType.FAILED, communicationRequestDB)
        );
    }
}
