package fr.insee.pearljam.domain.campaign.service.exception;

import fr.insee.pearljam.domain.shared.exception.EntityAlreadyExistException;

public class CampaignAlreadyExistException extends EntityAlreadyExistException {

	public static final String MESSAGE = "Campaign already exists";

	public CampaignAlreadyExistException() {
		super(MESSAGE);
	}
}
