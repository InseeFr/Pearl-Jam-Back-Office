package fr.insee.pearljam.domain.exception;

public class SurveyUnitNotFoundException extends EntityNotFoundException {

    public static final String MESSAGE = "Survey unit not found";

    public SurveyUnitNotFoundException() {
        super(MESSAGE);
    }
}
