package fr.insee.pearljam.api.dto.organizationunit;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrganizationUnitDto {
	private String id;
	private String label;

	public OrganizationUnitDto(String id, String label) {
		super();
		this.id = id;
		this.label = label;
	}
	
	public String toString() {
		return String.format("Id : %s - label : %s", id, label);
	}
}
