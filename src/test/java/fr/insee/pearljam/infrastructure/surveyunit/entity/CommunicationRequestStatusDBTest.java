package fr.insee.pearljam.infrastructure.surveyunit.entity;

import fr.insee.pearljam.domain.surveyunit.model.communication.*;
import fr.insee.pearljam.infrastructure.campaign.entity.CommunicationTemplateDB;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommunicationRequestStatusDBTest {
    @Test
    @DisplayName("Should return model object")
    void testToModel01() {
        CommunicationRequestStatusDB communicationRequestStatusDB = new CommunicationRequestStatusDB(1L, 123456789L, CommunicationStatusType.INITIATED, null);

        CommunicationRequestStatus communicationRequestStatus = CommunicationRequestStatusDB.toModel(communicationRequestStatusDB);
        assertThat(communicationRequestStatus.id()).isEqualTo(communicationRequestStatusDB.getId());
        assertThat(communicationRequestStatus.date()).isEqualTo(communicationRequestStatusDB.getDate());
        assertThat(communicationRequestStatus.status()).isEqualTo(communicationRequestStatusDB.getStatus());
    }

    @Test
    @DisplayName("Should return entity object")
    void testFromModel01() {
        CommunicationTemplateDB communicationTemplate = new CommunicationTemplateDB(1L, null, null, null, null);
        CommunicationRequestDB communicationRequestDB = new CommunicationRequestDB(null, communicationTemplate,
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
