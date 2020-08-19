package fr.insee.pearljam.api.dto.organizationunit;

import fr.insee.pearljam.api.domain.OrganizationUnit;

public class OrganizationUnitDto {
	private String id;
	private String label;

	public OrganizationUnitDto(String id, String label) {
		super();
		this.id = id;
		this.label = label;
	}
	
	public OrganizationUnitDto(OrganizationUnit ou) {
		super();
		this.id = ou.getId();
		this.label = ou.getLabel();
	}

	public OrganizationUnitDto() {
		super();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

}
