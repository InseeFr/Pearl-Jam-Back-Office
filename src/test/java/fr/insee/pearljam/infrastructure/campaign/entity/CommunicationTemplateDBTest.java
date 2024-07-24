package fr.insee.pearljam.infrastructure.campaign.entity;

import fr.insee.pearljam.domain.campaign.model.communication.CommunicationMedium;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommunicationTemplateDBTest {

    @Test
    @DisplayName("Should create model objects")
    void testToModel() {
        // Given
        CommunicationTemplateDB templateDB1 = new CommunicationTemplateDB(1L, "msg1", CommunicationMedium.EMAIL, CommunicationType.NOTICE);
        CommunicationTemplateDB templateDB2 = new CommunicationTemplateDB(2L, "msg2", CommunicationMedium.MAIL, CommunicationType.REMINDER);
        List<CommunicationTemplateDB> dbList = List.of(templateDB1, templateDB2);

        // When
        List<CommunicationTemplate> modelList = CommunicationTemplateDB.toModel(dbList);

        // Then
        assertThat(modelList).hasSize(2);

        assertThat(modelList.getFirst().id()).isEqualTo(1L);
        assertThat(modelList.getFirst().messhugahId()).isEqualTo("msg1");
        assertThat(modelList.getFirst().medium()).isEqualTo(CommunicationMedium.EMAIL);
        assertThat(modelList.getFirst().type()).isEqualTo(CommunicationType.NOTICE);

        assertThat(modelList.getLast().id()).isEqualTo(2L);
        assertThat(modelList.getLast().messhugahId()).isEqualTo("msg2");
        assertThat(modelList.getLast().medium()).isEqualTo(CommunicationMedium.MAIL);
        assertThat(modelList.getLast().type()).isEqualTo(CommunicationType.REMINDER);
    }

    @Test
    @DisplayName("Should create entity objects")
    void testFromModel() {
        // Given
        CommunicationTemplate template1 = new CommunicationTemplate(1L, "msg1", CommunicationMedium.EMAIL, CommunicationType.NOTICE);
        CommunicationTemplate template2 = new CommunicationTemplate(2L, "msg2", CommunicationMedium.MAIL, CommunicationType.REMINDER);
        List<CommunicationTemplate> modelList = List.of(template1, template2);

        // When
        List<CommunicationTemplateDB> dbList = CommunicationTemplateDB.fromModel(modelList);

        // Then
        assertThat(dbList).hasSize(2);

        assertThat(dbList.getFirst().getId()).isNull(); // ID is null because it's not set in fromModel method
        assertThat(dbList.getFirst().getMesshugahId()).isEqualTo("msg1");
        assertThat(dbList.getFirst().getMedium()).isEqualTo(CommunicationMedium.EMAIL);
        assertThat(dbList.getFirst().getType()).isEqualTo(CommunicationType.NOTICE);

        assertThat(dbList.getLast().getId()).isNull(); // ID is null because it's not set in fromModel method
        assertThat(dbList.getLast().getMesshugahId()).isEqualTo("msg2");
        assertThat(dbList.getLast().getMedium()).isEqualTo(CommunicationMedium.MAIL);
        assertThat(dbList.getLast().getType()).isEqualTo(CommunicationType.REMINDER);
    }
}
