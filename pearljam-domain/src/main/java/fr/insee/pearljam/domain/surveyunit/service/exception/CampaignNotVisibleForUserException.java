package fr.insee.pearljam.domain.surveyunit.service.exception;

import java.io.Serial;

public class CampaignNotVisibleForUserException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = -784002895484809004L;

    public static final String MESSAGE = "Campaign [%s] not visible for user [%s] ";

    public CampaignNotVisibleForUserException(String campaignId, String userId) {
        super(MESSAGE.formatted(campaignId, userId));
    }


}
