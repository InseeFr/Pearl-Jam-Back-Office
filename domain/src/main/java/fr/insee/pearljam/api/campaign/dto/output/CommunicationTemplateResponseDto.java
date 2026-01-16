package fr.insee.pearljam.api.campaign.dto.output;

import fr.insee.pearljam.domain.campaign.model.communication.CommunicationMedium;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;

import java.util.List;

public record CommunicationTemplateResponseDto(
        String id,
        String campaignId,
        String meshuggahId,
        CommunicationMedium medium,
        CommunicationType type
) {
    public static List<CommunicationTemplateResponseDto> fromModel(List<CommunicationTemplate> communicationTemplates) {
        return communicationTemplates.stream()
                .map(communicationTemplate ->
                        new CommunicationTemplateResponseDto(
                                communicationTemplate.meshuggahId(), //The id field corresponds to the meshuggahId to prevent regression.
                                communicationTemplate.campaignId(),
                                communicationTemplate.meshuggahId(),
                                communicationTemplate.medium(),
                                communicationTemplate.type()))
                .toList();
    }
}
