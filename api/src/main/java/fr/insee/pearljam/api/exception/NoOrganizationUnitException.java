package fr.insee.pearljam.api.exception;

import java.io.Serial;

public class NoOrganizationUnitException extends Exception {

	@Serial
	private static final long serialVersionUID = -4821409258862477873L;

	public NoOrganizationUnitException(String message) {
		super(message);
	}

}
