package fr.insee.pearljam.campaign.infrastructure.rest.dto.output;

import fr.insee.pearljam.campaign.domain.model.communication.CommunicationMedium;
import fr.insee.pearljam.campaign.domain.model.communication.CommunicationTemplate;
import fr.insee.pearljam.campaign.domain.model.communication.CommunicationType;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.output.CommunicationTemplateResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommunicationTemplateResponseDtoTest {

    @Test
    @DisplayName("Should return dto objects")
    void testFromModel() {
        // Given
        CommunicationTemplate template1 = new CommunicationTemplate("SIMPSONS2020X00", "msg1", CommunicationMedium.EMAIL, CommunicationType.NOTICE);
        CommunicationTemplate template2 = new CommunicationTemplate("SIMPSONS2020X00", "msg2", CommunicationMedium.LETTER, CommunicationType.REMINDER);
        List<CommunicationTemplate> communicationTemplates = List.of(template1, template2);

        // When
        List<CommunicationTemplateResponseDto> communicationDtos = CommunicationTemplateResponseDto.fromModel(communicationTemplates);

        // Then
        assertThat(communicationDtos).hasSize(2);

        CommunicationTemplateResponseDto dto1 = communicationDtos.getFirst();
        assertThat(dto1.medium()).isEqualTo(CommunicationMedium.EMAIL);
        assertThat(dto1.type()).isEqualTo(CommunicationType.NOTICE);

        CommunicationTemplateResponseDto dto2 = communicationDtos.get(1);
        assertThat(dto2.medium()).isEqualTo(CommunicationMedium.LETTER);
        assertThat(dto2.type()).isEqualTo(CommunicationType.REMINDER);
    }
}
