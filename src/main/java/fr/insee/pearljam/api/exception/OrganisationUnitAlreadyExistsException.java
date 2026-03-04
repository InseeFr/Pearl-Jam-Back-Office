package fr.insee.pearljam.api.exception;

import java.io.Serial;

public class OrganisationUnitAlreadyExistsException extends Exception {

	/**
	 *
	 */
	@Serial
	private static final long serialVersionUID = 1816091565879107055L;

	public OrganisationUnitAlreadyExistsException(String message) {
		super(message);
	}


}
