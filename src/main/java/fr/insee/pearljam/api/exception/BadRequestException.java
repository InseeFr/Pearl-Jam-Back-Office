package fr.insee.pearljam.api.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class BadRequestException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 8206024945370390220L;

	private final int code;
	private final String message;

	public BadRequestException(int code, String message) {
		this.code = code;
		this.message = message;
	}


}
