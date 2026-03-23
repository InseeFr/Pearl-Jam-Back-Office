package fr.insee.pearljam.api.dto.user;

import java.util.List;

import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
	@NotNull
	private String id;
	@NotNull
	private String firstName;
	@NotNull
	private String lastName;
	@NotNull
	@Valid
	private OrganizationUnitDto organizationUnit;
	private List<OrganizationUnitDto> localOrganizationUnits;

    public String toString() {
        String ou = organizationUnit != null ? organizationUnit.toString() : "null";
        return String.format("Id :%s - FirstName :%s - LastName :%s - Ou : %s", id, firstName, lastName, ou);
    }
}
