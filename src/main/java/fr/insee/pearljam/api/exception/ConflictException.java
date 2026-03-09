package fr.insee.pearljam.api.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class ConflictException extends Exception {

	@Serial
	private static final long serialVersionUID = -784002885474509004L;

	private final String globalMessage;

	public ConflictException(String globalMessage, String detailedMessage) {
		super(detailedMessage);
		this.globalMessage = globalMessage;
	}
}

