package fr.insee.pearljam.api.campaign.dto.input;

import fr.insee.pearljam.domain.campaign.model.communication.CommunicationMedium;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommunicationTemplateCreateDtoTest {

    @Test
    @DisplayName("Should return model objects")
    void testToModel() {
        // Given
        CommunicationTemplateCreateDto dto1 = new CommunicationTemplateCreateDto("SIMPSONS2020X00","msg1", CommunicationMedium.EMAIL, CommunicationType.NOTICE);
        CommunicationTemplateCreateDto dto2 = new CommunicationTemplateCreateDto("SIMPSONS2020X00","msg2", CommunicationMedium.LETTER, CommunicationType.REMINDER);
        List<CommunicationTemplateCreateDto> dtoList = List.of(dto1, dto2);

        // When
        List<CommunicationTemplate> modelList = CommunicationTemplateCreateDto.toModel(dtoList);

        // Then
        assertThat(modelList).hasSize(2);

        CommunicationTemplate firstTemplate = modelList.getFirst();
        assertThat(firstTemplate.campaignId()).isEqualTo("SIMPSONS2020X00");
        assertThat(firstTemplate.meshuggahId()).isEqualTo("msg1");
        assertThat(firstTemplate.medium()).isEqualTo(CommunicationMedium.EMAIL);
        assertThat(firstTemplate.type()).isEqualTo(CommunicationType.NOTICE);

        CommunicationTemplate lastTemplate = modelList.getLast();
        assertThat(lastTemplate.campaignId()).isEqualTo("SIMPSONS2020X00");
        assertThat(lastTemplate.meshuggahId()).isEqualTo("msg2");
        assertThat(lastTemplate.medium()).isEqualTo(CommunicationMedium.LETTER);
        assertThat(lastTemplate.type()).isEqualTo(CommunicationType.REMINDER);
    }
}
