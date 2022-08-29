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
	private String id;
	private String label;
	private OrganizationUnitType type;
	private List<UserContextDto> users;
	private List<String> organisationUnitRef;
	
	public OrganizationUnitContextDto(String id, String label, OrganizationUnitType type,
			List<UserContextDto> users) {
		super();
		this.id = id;
		this.label = label;
		this.type = type;
		this.users = users;
	}
	public OrganizationUnitContextDto() {
		super();
	}
	
	public OrganizationUnitContextDto(OrganizationUnit ou, List<User> lstUser, List<String> lstOURef) {
		this.id = ou.getId();
		this.label = ou.getLabel();
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
	public String getId() {
		return id;
	}
	/**
	 * @param id the organisationUnit id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the organisationUnitLabel
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the organisationUnit label to set
	 */
	public void setLabel(String label) {
		this.label = label;
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
