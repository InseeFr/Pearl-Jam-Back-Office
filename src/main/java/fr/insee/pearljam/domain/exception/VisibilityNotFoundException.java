package fr.insee.pearljam.domain.exception;

public class VisibilityNotFoundException extends EntityNotFoundException {

    public static final String MESSAGE = "Visibility not found";

    public VisibilityNotFoundException() {
        super(MESSAGE);
    }
}
