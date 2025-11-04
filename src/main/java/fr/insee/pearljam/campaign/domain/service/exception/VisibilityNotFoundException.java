package fr.insee.pearljam.campaign.domain.service.exception;

public class VisibilityNotFoundException extends RuntimeException {

    public static final String MESSAGE = "Visibility not found";

    public VisibilityNotFoundException() {
        super(MESSAGE);
    }
}
