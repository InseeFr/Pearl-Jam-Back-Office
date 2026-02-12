package fr.insee.pearljam.api.dto.organizationunit;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.api.domain.OrganizationUnit;
import fr.insee.pearljam.api.domain.OrganizationUnitType;
import fr.insee.pearljam.api.domain.User;
import fr.insee.pearljam.api.dto.user.UserContextDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
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

	public OrganizationUnitContextDto(OrganizationUnit ou, List<User> lstUser, List<String> lstOURef) {
		this.id = ou.getId();
		this.label = ou.getLabel();
		this.type = ou.getType();
		this.users = lstUser.stream().map(u -> new UserContextDto(u.getId(), u.getFirstName(), u.getLastName(), null,
				null)).toList();
		if (lstOURef.isEmpty()) {
			this.organisationUnitRef = null;
		} else {
			this.organisationUnitRef = lstOURef;
		}
	}

}
