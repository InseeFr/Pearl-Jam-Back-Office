package fr.insee.pearljam.api.dto.contactoutcome;

import java.util.List;

public class ContactOutcomeTypeCountCampaignDto {
	
	private List<ContactOutcomeTypeCountDto> organizationUnits;
	
	private ContactOutcomeTypeCountDto france;
	
	/**
	 * @param organizationUnits
	 * @param france
	 */
	public ContactOutcomeTypeCountCampaignDto(List<ContactOutcomeTypeCountDto> organizationUnits, ContactOutcomeTypeCountDto france) {
		super();
		this.organizationUnits = organizationUnits;
		this.france = france;
	}

	public ContactOutcomeTypeCountCampaignDto() {
		super();
	}

	/**
	 * @return the organizationUnits
	 */
	public List<ContactOutcomeTypeCountDto> getOrganizationUnits() {
		return organizationUnits;
	}

	/**
	 * @return the france
	 */
	public ContactOutcomeTypeCountDto getFrance() {
		return france;
	}

	/**
	 * @param organizationUnits the organizationUnits to set
	 */
	public void setOrganizationUnits(List<ContactOutcomeTypeCountDto> organizationUnits) {
		this.organizationUnits = organizationUnits;
	}

	/**
	 * @param france the france to set
	 */
	public void setFrance(ContactOutcomeTypeCountDto france) {
		this.france = france;
	}

}
