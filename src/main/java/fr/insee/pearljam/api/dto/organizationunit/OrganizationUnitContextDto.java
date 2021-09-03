package fr.insee.pearljam.api.dto.organizationunit;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.domain.OrganizationUnit;
import fr.insee.pearljam.api.domain.OrganizationUnitType;
import fr.insee.pearljam.api.domain.User;
import fr.insee.pearljam.api.dto.user.UserContextDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationUnitContextDto {
	private String organisationUnit;
	private String organisationUnitLabel;
	private OrganizationUnitType type;
	private List<UserContextDto> users;
	private List<String> organisationUnitRef;
	
	public OrganizationUnitContextDto(String organisationUnit, String organisationUnitLabel, OrganizationUnitType type,
			List<UserContextDto> users) {
		super();
		this.organisationUnit = organisationUnit;
		this.organisationUnitLabel = organisationUnitLabel;
		this.type = type;
		this.users = users;
	}
	public OrganizationUnitContextDto() {
		super();
	}
	
	public OrganizationUnitContextDto(OrganizationUnit ou, List<User> lstUser, List<String> lstOURef) {
		this.organisationUnit = ou.getId();
		this.organisationUnitLabel = ou.getLabel();
		this.type = ou.getType();
		this.users = lstUser.stream().map(u -> new UserContextDto(u.getId(), u.getFirstName(), u.getLastName(), null, null)).collect(Collectors.toList());
		if(lstOURef.isEmpty()) {
			this.organisationUnitRef = null;
		} else {
			this.organisationUnitRef = lstOURef;
		}
	}
	/**
	 * @return the organisationUnit
	 */
	public String getOrganisationUnit() {
		return organisationUnit;
	}
	/**
	 * @param organisationUnit the organisationUnit to set
	 */
	public void setOrganisationUnit(String organisationUnit) {
		this.organisationUnit = organisationUnit;
	}
	/**
	 * @return the organisationUnitLabel
	 */
	public String getOrganisationUnitLabel() {
		return organisationUnitLabel;
	}
	/**
	 * @param organisationUnitLabel the organisationUnitLabel to set
	 */
	public void setOrganisationUnitLabel(String organisationUnitLabel) {
		this.organisationUnitLabel = organisationUnitLabel;
	}
	/**
	 * @return the type
	 */
	public OrganizationUnitType getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(OrganizationUnitType type) {
		this.type = type;
	}
	/**
	 * @return the users
	 */
	public List<UserContextDto> getUsers() {
		return users;
	}
	/**
	 * @param users the users to set
	 */
	public void setUsers(List<UserContextDto> users) {
		this.users = users;
	}
	public List<String> getOrganisationUnitRef() {
		return organisationUnitRef;
	}
	public void setOrganisationUnitRef(List<String> organisationUnitRef) {
		this.organisationUnitRef = organisationUnitRef;
	}
	
	
}
