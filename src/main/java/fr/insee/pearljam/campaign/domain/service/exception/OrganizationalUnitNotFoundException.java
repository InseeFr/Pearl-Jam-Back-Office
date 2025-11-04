package fr.insee.pearljam.campaign.domain.service.exception;

import fr.insee.pearljam.shared.exception.EntityNotFoundException;

public class OrganizationalUnitNotFoundException extends EntityNotFoundException {

    public static final String MESSAGE = "Organizational unit not found";

    public OrganizationalUnitNotFoundException() {
        super(MESSAGE);
    }
}
