package fr.insee.pearljam.api.dto.user;

import java.util.List;

import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;

public class UserDto {
	private String id;
	private String firstName;
	private String lastName;
	private List<OrganizationUnitDto> organizationUnits;

	public UserDto(String id, String firstName, String lastName, List<OrganizationUnitDto> organizationUnits) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.organizationUnits = organizationUnits;
	}

	public UserDto() {
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
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the organizationUnits
	 */
	public List<OrganizationUnitDto> getOrganizationUnits() {
		return organizationUnits;
	}

	/**
	 * @param organizationUnits the organizationUnits to set
	 */
	public void setOrganizationUnits(List<OrganizationUnitDto> organizationUnits) {
		this.organizationUnits = organizationUnits;
	}

}
