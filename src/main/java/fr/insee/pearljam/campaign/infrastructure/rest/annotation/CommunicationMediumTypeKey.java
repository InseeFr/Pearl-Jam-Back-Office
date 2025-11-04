package fr.insee.pearljam.campaign.infrastructure.rest.annotation;

import fr.insee.pearljam.campaign.domain.model.communication.CommunicationMedium;
import fr.insee.pearljam.campaign.domain.model.communication.CommunicationType;

public record CommunicationMediumTypeKey(
    CommunicationMedium medium,
    CommunicationType type) {
}
