package fr.insee.pearljam.domain.campaign.service.exception;

import fr.insee.pearljam.domain.shared.exception.EntityNotFoundException;

public class OrganizationalUnitNotFoundException extends EntityNotFoundException {

    public static final String MESSAGE = "Organizational unit not found";

    public OrganizationalUnitNotFoundException() {
        super(MESSAGE);
    }
}
