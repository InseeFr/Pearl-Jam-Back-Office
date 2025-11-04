package fr.insee.pearljam.campaign.domain.service.exception;

import fr.insee.pearljam.shared.exception.EntityAlreadyExistException;

public class CampaignAlreadyExistException extends EntityAlreadyExistException {

	public static final String MESSAGE = "Campaign already exists";

	public CampaignAlreadyExistException() {
		super(MESSAGE);
	}
}
