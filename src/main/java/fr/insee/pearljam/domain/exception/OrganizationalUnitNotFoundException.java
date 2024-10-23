package fr.insee.pearljam.domain.exception;

public class OrganizationalUnitNotFoundException extends EntityNotFoundException {

    public static final String MESSAGE = "Organizational unit not found";

    public OrganizationalUnitNotFoundException() {
        super(MESSAGE);
    }
}
