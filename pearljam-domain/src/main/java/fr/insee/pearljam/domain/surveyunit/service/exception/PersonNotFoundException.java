package fr.insee.pearljam.domain.surveyunit.service.exception;

import fr.insee.pearljam.domain.shared.exception.EntityNotFoundException;

public class PersonNotFoundException extends EntityNotFoundException {

    public static final String MESSAGE = "Person not found";

    public PersonNotFoundException() {
        super(MESSAGE);
    }
}
