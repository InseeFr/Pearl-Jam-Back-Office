package fr.insee.pearljam.api.exception;

import java.io.Serial;

public class NotFoundException extends Exception {

	@Serial
	private static final long serialVersionUID = -784002885484509004L;

	public NotFoundException(String message) {
		super(message);
	}


}
