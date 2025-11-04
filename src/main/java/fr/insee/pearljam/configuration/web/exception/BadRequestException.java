package fr.insee.pearljam.configuration.web.exception;

import java.io.Serial;

public class BadRequestException extends Exception {

	@Serial
	private static final long serialVersionUID = -4821409258862477873L;

	public BadRequestException(String message) {
		super(message);
	}

}
