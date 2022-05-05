package fr.insee.pearljam.api.dto.user;

import java.util.List;

import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;

public class UserDto {
	private String id;
	private String firstName;
	private String lastName;
	private OrganizationUnitDto organizationUnit;
	private List<OrganizationUnitDto> localOrganizationUnits;

	public UserDto(String id, String firstName, String lastName, OrganizationUnitDto organizationUnit, List<OrganizationUnitDto> localOrganizationUnits) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.organizationUnit = organizationUnit;
		this.localOrganizationUnits = localOrganizationUnits;
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
	public OrganizationUnitDto getOrganizationUnit() {
		return organizationUnit;
	}

	/**
	 * @param organizationUnits the organizationUnits to set
	 */
	public void setOrganizationUnit(OrganizationUnitDto organizationUnit) {
		this.organizationUnit = organizationUnit;
	}

	/**
	 * @return the localOrganisationUnits
	 */
	public List<OrganizationUnitDto> getLocalOrganizationUnits() {
		return localOrganizationUnits;
	}

	/**
	 * @param localOrganisationUnits the localOrganisationUnits to set
	 */
	public void setLocalOrganizationUnits(List<OrganizationUnitDto> localOrganizationUnits) {
		this.localOrganizationUnits = localOrganizationUnits;
	}

	public String toString() {
		String ou = organizationUnit != null ? organizationUnit.toString() : "null";
		return String.format("Id :%s - FirstName :%s - LastName :%s - Ou : %s", id, firstName, lastName, ou);
	}
}
