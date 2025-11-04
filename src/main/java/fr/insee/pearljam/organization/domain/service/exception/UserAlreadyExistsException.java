package fr.insee.pearljam.organization.domain.service.exception;

import java.io.Serial;

public class UserAlreadyExistsException extends Exception {

	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 1816091565879107055L;

	public UserAlreadyExistsException(String message) {
		super(message);
	}


}
