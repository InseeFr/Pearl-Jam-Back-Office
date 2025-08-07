package fr.insee.pearljam.api.dto.user;

import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String id;
    private String firstName;
    private String lastName;
    private OrganizationUnitDto organizationUnit;
    private List<OrganizationUnitDto> localOrganizationUnits;

    public String toString() {
		String ou = organizationUnit != null ? organizationUnit.toString() : "null";
		return String.format("Id :%s - FirstName :%s - LastName :%s - Ou : %s", id, firstName, lastName, ou);
	}
}
