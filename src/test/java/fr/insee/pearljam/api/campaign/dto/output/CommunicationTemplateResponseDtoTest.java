package fr.insee.pearljam.api.campaign.dto.output;

import fr.insee.pearljam.domain.campaign.model.communication.CommunicationMedium;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommunicationTemplateResponseDtoTest {

    @Test
    @DisplayName("Should return dto objects")
    void testFromModel() {
        // Given
        CommunicationTemplate template1 = new CommunicationTemplate(1L, "msg1", CommunicationMedium.EMAIL, CommunicationType.NOTICE);
        CommunicationTemplate template2 = new CommunicationTemplate(2L, "msg2", CommunicationMedium.LETTER, CommunicationType.REMINDER);
        List<CommunicationTemplate> communicationTemplates = List.of(template1, template2);

        // When
        List<CommunicationTemplateResponseDto> communicationDtos = CommunicationTemplateResponseDto.fromModel(communicationTemplates);

        // Then
        assertThat(communicationDtos).hasSize(2);

        CommunicationTemplateResponseDto dto1 = communicationDtos.get(0);
        assertThat(dto1.id()).isEqualTo(1L);
        assertThat(dto1.messhugahId()).isEqualTo("msg1");
        assertThat(dto1.medium()).isEqualTo(CommunicationMedium.EMAIL);
        assertThat(dto1.type()).isEqualTo(CommunicationType.NOTICE);

        CommunicationTemplateResponseDto dto2 = communicationDtos.get(1);
        assertThat(dto2.id()).isEqualTo(2L);
        assertThat(dto2.messhugahId()).isEqualTo("msg2");
        assertThat(dto2.medium()).isEqualTo(CommunicationMedium.LETTER);
        assertThat(dto2.type()).isEqualTo(CommunicationType.REMINDER);
    }
}
