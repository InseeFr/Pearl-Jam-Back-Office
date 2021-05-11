package fr.insee.pearljam.api.dto.visibility;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VisibilityContextDto extends VisibilityDto {
	
	/**
	 * Organizational unit of the visibility
	 */
	private String organizationalUnit;


	public VisibilityContextDto() {
		super();
	}


	public String getOrganizationalUnit() {
		return organizationalUnit;
	}



	public void setOrganizationalUnit(String organizationalUnit) {
		this.organizationalUnit = organizationalUnit;
	}




}
