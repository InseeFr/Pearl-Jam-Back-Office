package fr.insee.pearljam.api.dto.state;

import java.util.List;

public class StateCountCampaignDto {
	
	private List<StateCountDto> organizationUnits;
	
	private StateCountDto france;

	/**
	 * @param organizationUnits
	 * @param france
	 */
	public StateCountCampaignDto(List<StateCountDto> organizationUnits, StateCountDto france) {
		super();
		this.organizationUnits = organizationUnits;
		this.france = france;
	}

	public StateCountCampaignDto() {
		super();
	}

	/**
	 * @return the organizationUnits
	 */
	public List<StateCountDto> getOrganizationUnits() {
		return organizationUnits;
	}

	/**
	 * @param organizationUnits the organizationUnits to set
	 */
	public void setOrganizationUnits(List<StateCountDto> organizationUnits) {
		this.organizationUnits = organizationUnits;
	}

	/**
	 * @return the france
	 */
	public StateCountDto getFrance() {
		return france;
	}

	/**
	 * @param france the france to set
	 */
	public void setFrance(StateCountDto france) {
		this.france = france;
	}

}
