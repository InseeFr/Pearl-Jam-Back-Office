package fr.insee.pearljam.api.dto.user;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserContextDto {

	private String id;
	private String firstName;
	private String lastName;
	private OrganizationUnitDto organizationUnit;
	private List<OrganizationUnitDto> localOrganizationUnits;

}
