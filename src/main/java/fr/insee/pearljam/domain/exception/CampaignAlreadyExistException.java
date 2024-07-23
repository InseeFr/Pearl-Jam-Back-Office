package fr.insee.pearljam.domain.exception;

public class CampaignAlreadyExistException extends EntityAlreadyExistException {

	public static final String MESSAGE = "Campaign already exists";

	public CampaignAlreadyExistException() {
		super(MESSAGE);
	}
}
