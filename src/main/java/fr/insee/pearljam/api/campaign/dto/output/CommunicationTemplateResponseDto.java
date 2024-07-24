package fr.insee.pearljam.api.campaign.dto.output;

import fr.insee.pearljam.domain.campaign.model.communication.CommunicationMedium;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;

import java.util.List;

public record CommunicationTemplateResponseDto(
        Long id,
        String messhugahId,
        CommunicationMedium medium,
        CommunicationType type
) {
    public static List<CommunicationTemplateResponseDto> fromModel(List<CommunicationTemplate> communicationTemplates) {
        return communicationTemplates.stream()
                .map(communicationTemplate ->
                        new CommunicationTemplateResponseDto(
                                communicationTemplate.id(),
                                communicationTemplate.messhugahId(),
                                communicationTemplate.medium(),
                                communicationTemplate.type()))
                .toList();
    }
}
