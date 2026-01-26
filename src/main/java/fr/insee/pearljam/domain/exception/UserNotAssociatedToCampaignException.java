package fr.insee.pearljam.domain.exception;

import java.io.Serial;

public class UserNotAssociatedToCampaignException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -784002885484809004L;

    public static final String MESSAGE = "[%s] campaign is not associated to the user [%s]";

    public UserNotAssociatedToCampaignException(String campaignId, String userId) {
        super(MESSAGE.formatted(campaignId, userId));
    }
}
