package fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity;

import fr.insee.pearljam.surveyunit.domain.model.communication.CommunicationRequestEmitter;
import fr.insee.pearljam.surveyunit.domain.model.communication.CommunicationRequestReason;
import fr.insee.pearljam.surveyunit.domain.model.communication.CommunicationRequestStatus;
import fr.insee.pearljam.surveyunit.domain.model.communication.CommunicationStatusType;
import static org.assertj.core.api.Assertions.assertThat;

import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.entity.CommunicationTemplateDBId;
import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.CommunicationRequestDB;
import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.CommunicationRequestStatusDB;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommunicationRequestStatusDBTest {
    @Test
    @DisplayName("Should return model object")
    void testToModel01() {
        CommunicationRequestStatusDB communicationRequestStatusDB = new CommunicationRequestStatusDB(1L, 123456789L, CommunicationStatusType.INITIATED, null);

        CommunicationRequestStatus communicationRequestStatus = CommunicationRequestStatusDB.toModel(communicationRequestStatusDB);

        assertThat(communicationRequestStatus.date()).isEqualTo(communicationRequestStatusDB.getDate());
        assertThat(communicationRequestStatus.status()).isEqualTo(communicationRequestStatusDB.getStatus());
    }

    @Test
    @DisplayName("Should return entity object")
    void testFromModel01() {
        CommunicationRequestDB communicationRequestDB = new CommunicationRequestDB(null, new CommunicationTemplateDBId("mesh1","SIMPSONS2020X00"),
                CommunicationRequestReason.UNREACHABLE,
                CommunicationRequestEmitter.INTERVIEWER, null, null);
        CommunicationRequestStatus communicationRequestStatus = new CommunicationRequestStatus(1L, 123456789L, CommunicationStatusType.INITIATED);

        CommunicationRequestStatusDB communicationRequestStatusDB = CommunicationRequestStatusDB.fromModel(communicationRequestStatus, communicationRequestDB);
        assertThat(communicationRequestStatusDB.getId()).isEqualTo(communicationRequestStatus.id());
        assertThat(communicationRequestStatusDB.getDate()).isEqualTo(communicationRequestStatus.date());
        assertThat(communicationRequestStatusDB.getStatus()).isEqualTo(communicationRequestStatus.status());
        assertThat(communicationRequestStatusDB.getCommunicationRequest()).isEqualTo(communicationRequestDB);
    }
}
