package fr.insee.pearljam.domain.campaign.service.exception;

public class CampaignNotFoundExceptionRuntime extends RuntimeException {

    public static final String MESSAGE = "Campaign not found";

    public CampaignNotFoundExceptionRuntime() {
        super(MESSAGE);
    }
}
