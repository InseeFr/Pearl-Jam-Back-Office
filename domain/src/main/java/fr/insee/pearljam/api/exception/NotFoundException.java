package fr.insee.pearljam.api.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class NotFoundException extends Exception {

	@Serial
	private static final long serialVersionUID = -784002885484509004L;

	private final String globalMessage;

	public NotFoundException(String detailedMessage) {
		super(detailedMessage);
		this.globalMessage = null;
	}

	public NotFoundException(String globalMessage, String detailedMessage) {
		super(detailedMessage);
		this.globalMessage = globalMessage;
	}
}
