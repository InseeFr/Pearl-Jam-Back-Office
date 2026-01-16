package fr.insee.pearljam.api.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The possible types of a State entity
 * @author scorcaud
 *
 */
@Getter
@RequiredArgsConstructor
public enum StateType {
	
	NVM("Not visible to management"),
	NNS("Not Assigned, not started"),
	ANV("Assigned Not visible to interviewer"),
	VIN("Visible to the interviewer and not clickable"),
	VIC("Visible to the interviewer and clickable"),
	PRC("Preparing contact"), 
	AOC("At least one contact made"),
	APS("Appointment scheduled"), 
	INS("Interview started"), 
	WFT("Waiting for transmission"),
	WFS("Waiting for synchronization"), 
	TBR("To be reviewed"), 
	FIN("Finalized"),
	CLO("Closed"),
	NVA("Not Available to All");

	private final String label;

}
