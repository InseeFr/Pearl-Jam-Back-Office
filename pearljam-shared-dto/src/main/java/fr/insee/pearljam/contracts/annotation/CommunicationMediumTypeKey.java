package fr.insee.pearljam.contracts.annotation;

import fr.insee.pearljam.domain.campaign.model.communication.CommunicationMedium;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;

public record CommunicationMediumTypeKey(
    CommunicationMedium medium,
    CommunicationType type) {
}
