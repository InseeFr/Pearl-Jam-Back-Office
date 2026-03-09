package fr.insee.pearljam.api.organizationunit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.domain.organizationunit.model.OrganizationUnitType;
import fr.insee.pearljam.api.organizationunit.dto.user.UserContextDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationUnitContextDto {
	@NotBlank
	private String id;
	@NotBlank
	private String label;
	@NotNull
	private OrganizationUnitType type;
	private List<UserContextDto> users;
	private List<String> organisationUnitRef;

	public OrganizationUnitContextDto(String id, String label, OrganizationUnitType type,
									  List<UserContextDto> users) {
		this(id, label, type, users, null);
	}

	public OrganizationUnitContextDto(String id, String label, OrganizationUnitType type,
									  List<UserContextDto> users, List<String> organisationUnitRef) {
		this.id = id;
		this.label = label;
		this.type = type;
		this.users = users;
		this.organisationUnitRef = organisationUnitRef == null || organisationUnitRef.isEmpty() ? null : organisationUnitRef;
	}

}
