package fr.insee.pearljam.domain.exception;

public class CampaignOnGoingException extends Exception {

	public static final String MESSAGE = "Campaign is on-going and can't be deleted";

	public CampaignOnGoingException() {
		super(MESSAGE);
	}
}
