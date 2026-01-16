package fr.insee.pearljam.domain.exception;

public class PersonNotFoundException extends EntityNotFoundException {

    public static final String MESSAGE = "Person not found";

    public PersonNotFoundException() {
        super(MESSAGE);
    }
}
