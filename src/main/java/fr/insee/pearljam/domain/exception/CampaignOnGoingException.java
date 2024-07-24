package fr.insee.pearljam.domain.exception;

public class CampaignOnGoingException extends Exception {

	public static final String MESSAGE = "Campaign is ongoing";

	public CampaignOnGoingException() {
		super(MESSAGE);
	}
}
