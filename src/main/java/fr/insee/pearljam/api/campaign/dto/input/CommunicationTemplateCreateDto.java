package fr.insee.pearljam.api.campaign.dto.input;

import fr.insee.pearljam.domain.campaign.model.communication.CommunicationMedium;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing the communication template of a visibility.
 *
 * @param meshuggahId  The identifier for Messhugah
 * @param medium       The medium of communication
 * @param type         The type of communication
 */
public record CommunicationTemplateCreateDto(
        @NotNull
        String meshuggahId,
        @NotNull
        CommunicationMedium medium,
        @NotNull
        CommunicationType type
) {
    public static List<CommunicationTemplate> toModel(List<CommunicationTemplateCreateDto> communicationTemplates) {
        if(communicationTemplates == null) {
            return new ArrayList<>();
        }
        return communicationTemplates.stream()
                .map(CommunicationTemplateCreateDto::toModel)
                .toList();
    }

    public static CommunicationTemplate toModel(CommunicationTemplateCreateDto communicationTemplate) {
        if(communicationTemplate == null) {
            return null;
        }
        return new CommunicationTemplate(
                null,
                communicationTemplate.meshuggahId(),
                communicationTemplate.medium(),
                communicationTemplate.type());
    }
}
