package fr.insee.pearljam.api.campaign.dto.input;

import fr.insee.pearljam.domain.campaign.model.communication.CommunicationMedium;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * A class representing the communication template of a visibility.
 *
 * @param messhugahId  The identifier for Messhugah
 * @param medium       The medium of communication
 * @param type         The type of communication
 */
public record CommunicationTemplateCreateDto(
        @NotNull
        String messhugahId,
        @NotNull
        CommunicationMedium medium,
        @NotNull
        CommunicationType type
) {
    public static List<CommunicationTemplate> toModel(List<CommunicationTemplateCreateDto> communicationTemplates) {
        return communicationTemplates.stream()
                .map(communicationTemplate ->
                        new CommunicationTemplate(
                                null,
                                communicationTemplate.messhugahId(),
                                communicationTemplate.medium(),
                                communicationTemplate.type()))
                .toList();
    }
}
