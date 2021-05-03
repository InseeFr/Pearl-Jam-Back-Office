package fr.insee.pearljam.api.exception;

public class UserAlreadyExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1816091565879107055L;

	public UserAlreadyExistsException(String message) {
		super(message);
	}


}
