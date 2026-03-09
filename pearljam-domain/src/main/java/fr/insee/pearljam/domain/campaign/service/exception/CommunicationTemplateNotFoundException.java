package fr.insee.pearljam.domain.campaign.service.exception;

public class CommunicationTemplateNotFoundException extends RuntimeException {

	public static final String MESSAGE = "Communication template not found";

	public CommunicationTemplateNotFoundException() {
		super(MESSAGE);
	}
}
