package fr.insee.pearljam.campaign.domain.service.exception;

import fr.insee.pearljam.shared.exception.EntityNotFoundException;

public class CampaignNotFoundException extends EntityNotFoundException {

    public static final String MESSAGE = "Campaign not found";

    public CampaignNotFoundException() {
        super(MESSAGE);
    }
}
