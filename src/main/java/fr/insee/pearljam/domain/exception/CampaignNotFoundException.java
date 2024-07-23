package fr.insee.pearljam.domain.exception;

public class CampaignNotFoundException extends EntityNotFoundException {

    public static final String MESSAGE = "Campaign not found";

    public CampaignNotFoundException() {
        super(MESSAGE);
    }
}
