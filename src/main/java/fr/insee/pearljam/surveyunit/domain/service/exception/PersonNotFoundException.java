package fr.insee.pearljam.surveyunit.domain.service.exception;

import fr.insee.pearljam.shared.exception.EntityNotFoundException;

public class PersonNotFoundException extends EntityNotFoundException {

    public static final String MESSAGE = "Person not found";

    public PersonNotFoundException() {
        super(MESSAGE);
    }
}
