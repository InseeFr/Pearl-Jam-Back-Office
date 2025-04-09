package fr.insee.pearljam.api.campaign.dto.input;

import fr.insee.pearljam.domain.campaign.model.communication.CommunicationMedium;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public static List<CommunicationTemplate> toModel(List<CommunicationTemplateCreateDto> communicationTemplates, String campaignId) {
        if(communicationTemplates == null) {
            return new ArrayList<>();
        }
      return communicationTemplates.stream()
          .map(template -> toModel(template, campaignId))
          .toList();
    }

    public static CommunicationTemplate toModel(CommunicationTemplateCreateDto communicationTemplate, String campaignId) {
        if(communicationTemplate == null) {
            return null;
        }
        return new CommunicationTemplate(
                campaignId,
                communicationTemplate.meshuggahId(),
                communicationTemplate.medium(),
                communicationTemplate.type());
    }
}
